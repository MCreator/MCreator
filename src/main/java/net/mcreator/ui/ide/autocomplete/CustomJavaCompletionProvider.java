/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.mcreator.ui.ide.autocomplete;

import net.mcreator.java.ImportTreeBuilder;
import net.mcreator.java.JavaConventions;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.workspace.Workspace;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;
import org.fife.rsta.ac.java.rjc.lexer.Token;
import org.fife.ui.autocomplete.*;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import java.io.StringReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CustomJavaCompletionProvider extends DefaultCompletionProvider {

	private final Workspace workspace;
	@Nullable private final StringCompletitionProvider stringProvider;

	private static Map<String, List<String>> cachedImportTree = null;
	private static long lastImportTreeUpdate = 0;

	private record ClassInfo(String pkg, boolean isInterface, boolean isEnum) {
		static ClassInfo of(String fqdn) {
			String pkg = fqdn.contains(".") ? fqdn.substring(0, fqdn.lastIndexOf('.')) : "";
			boolean inf = false, enm = false;
			try {
				Class<?> clazz = Class.forName(fqdn);
				inf = clazz.isInterface();
				enm = clazz.isEnum();
			} catch (Throwable ignored) {
			}
			return new ClassInfo(pkg, inf, enm);
		}
	}

	private static final Map<String, ClassInfo> CLASS_INFO_CACHE = new ConcurrentHashMap<>();

	private static ClassInfo getClassInfo(String fqdn) {
		return CLASS_INFO_CACHE.computeIfAbsent(fqdn, ClassInfo::of);
	}

	public CustomJavaCompletionProvider(Workspace workspace) {
		this.workspace = workspace;
		this.stringProvider = workspace != null ? new StringCompletitionProvider(workspace) : null;
		setAutoActivationRules(true, ".");
	}

	@Override
	public String getAlreadyEnteredText(JTextComponent comp) {
		String word = super.getAlreadyEnteredText(comp);
		if (word == null) word = "";
		try {
			int caret = comp.getCaretPosition();
			String textBefore = comp.getText(0, Math.max(0, caret - word.length())).trim();
			if (textBefore.endsWith("Blocks.")) return "Blocks." + word;
			if (textBefore.endsWith("Items.")) return "Items." + word;
		} catch (BadLocationException ignored) {
		}
		return word;
	}

	@Override
	protected List<Completion> getCompletionsImpl(JTextComponent comp) {
		List<Completion> completions = new ArrayList<>();
		if (!PreferencesManager.PREFERENCES.ide.autocomplete.get()) {
			return completions;
		}

		String mode = PreferencesManager.PREFERENCES.ide.autocompleteMode.get();

		Document doc = comp.getDocument();
		int caretPos = comp.getCaretPosition();
		String code;
		try {
			code = doc.getText(0, doc.getLength());
		} catch (BadLocationException e) {
			return completions;
		}

		String codeBeforeCursor = code.substring(0, Math.min(caretPos, code.length()));

		Element root = doc.getDefaultRootElement();
		int lineIndex = root.getElementIndex(caretPos);
		Element lineElem = root.getElement(lineIndex);
		int lineStart = lineElem.getStartOffset();
		String lineUntilPosition;
		try {
			lineUntilPosition = doc.getText(lineStart, caretPos - lineStart);
		} catch (BadLocationException e) {
			lineUntilPosition = "";
		}

		String alreadyEntered = getAlreadyEnteredText(comp);
		String wordOnly = alreadyEntered.contains(".") ? alreadyEntered.substring(alreadyEntered.lastIndexOf('.') + 1) : alreadyEntered;

		String textBeforeWord = lineUntilPosition.substring(0, Math.max(0, lineUntilPosition.length() - wordOnly.length()));
		boolean isDotContext = textBeforeWord.trim().endsWith(".");

		if ("Trigger on dot".equals(mode) && !isDotContext) {
			return completions;
		}

		if (isDotContext) {
			String beforeDot = textBeforeWord.substring(0, textBeforeWord.lastIndexOf('.')).trim();
			String targetName = extractTargetName(beforeDot);
			if (!targetName.isEmpty()) {
				List<JavaTypeResolver.CompletionItem> items = JavaTypeResolver.getCompletionsFor(targetName, code, codeBeforeCursor, workspace);
				addResolverItems(items, wordOnly, alreadyEntered.startsWith("Blocks.") || alreadyEntered.startsWith("Items."), completions);
			}
		} else {
			// General completions
			List<JavaTypeResolver.CompletionItem> thisItems = JavaTypeResolver.getCompletionsFor("this", code, codeBeforeCursor, workspace);
			addResolverItems(thisItems, wordOnly, false, completions);

			// Keywords
			for (String kw : JavaConventions.JAVA_RESERVED_WORDS) {
				if (matchesFilter(kw, wordOnly)) {
					completions.add(new JavaKeywordCompletition(this, kw));
				}
			}

			// Localization keys
			if (stringProvider != null) {
				List<Completion> stringComps = stringProvider.getCompletions(comp);
				if (stringComps != null) {
					for (Completion sc : stringComps) {
						if (matchesFilter(sc.getInputText(), wordOnly)) {
							completions.add(sc);
						}
					}
				}
			}

			// Document words extracted via RSTA Java Lexer
			Set<String> addedWords = new HashSet<>(JavaConventions.JAVA_RESERVED_WORDS);
			try {
				Scanner scanner = new Scanner(new StringReader(code));
				Token t;
				while ((t = scanner.yylex()) != null) {
					if (t.isIdentifier()) {
						String w = t.getLexeme();
						if (w != null && w.length() > 1 && addedWords.add(w)) {
							if (matchesFilter(w, wordOnly)) {
								CustomVariableCompletion cvc = new CustomVariableCompletion(this, w);
								completions.add(cvc);
							}
						}
					}
				}
			} catch (Throwable ignored) {
			}

			// External classes & workspace classes
			String trimmedBefore = textBeforeWord.trim();
			boolean isClassContext = trimmedBefore.endsWith("@") ||
					(!wordOnly.isEmpty() && Character.isUpperCase(wordOnly.charAt(0)));
			if (!isClassContext) {
				for (String kw : new String[]{"new", "extends", "implements", "import", "class", "interface", "enum"}) {
					if (trimmedBefore.endsWith(kw)) {
						isClassContext = true;
						break;
					}
				}
			}

			if (isClassContext && workspace != null && workspace.getGenerator() != null) {
				Map<String, List<String>> tree = getImportTreeCached(workspace);
				if (tree != null) {
					Set<String> addedFQDNs = new HashSet<>();
					for (Map.Entry<String, List<String>> entry : tree.entrySet()) {
						String className = entry.getKey();
						if (matchesFilter(className, wordOnly)) {
							List<String> fqdns = entry.getValue();
							if (fqdns != null && !fqdns.isEmpty()) {
								for (String fqdn : fqdns) {
									if (addedFQDNs.add(fqdn)) {
										ClassInfo info = getClassInfo(fqdn);
										CustomClassCompletion ccc = new CustomClassCompletion(this, className, info.pkg, info.isInterface, info.isEnum);
										completions.add(ccc);
									}
								}
							}
						}
					}
				}
			}
		}

		completions.sort((c1, c2) -> {
			int r1 = c1.getRelevance();
			int r2 = c2.getRelevance();
			if (r1 != r2) return Integer.compare(r2, r1);
			int cmp = c1.toString().compareTo(c2.toString());
			if (cmp != 0) return cmp;
			return Integer.compare(System.identityHashCode(c1), System.identityHashCode(c2));
		});

		return completions;
	}

	private void addResolverItems(List<JavaTypeResolver.CompletionItem> items, String wordOnly, boolean isBlocksContext, List<Completion> completions) {
		for (JavaTypeResolver.CompletionItem item : items) {
			String methodName = item.label.contains("(") ? item.label.substring(0, item.label.indexOf('(')) : item.label;
			if (matchesFilter(methodName, wordOnly) || (item.kind.equals("field") && matchesFilter(item.insertText, wordOnly))) {
				if (item.kind.equals("method")) {
					String template = item.insertText.replaceAll("\\$\\{\\d+:", "\\${");
					if (!template.contains("${")) {
						template = template + "${cursor}";
					}

					String docStr = item.docSummary != null ? item.docSummary : item.detail + " " + item.label;

					completions.add(new CustomMethodCompletion(this, methodName, item.label, item.detail, item.declaringClass, template, docStr, item.visibility, item.isStatic, item.isAbstract, item.isDeprecated));
				} else {
					completions.add(new CustomFieldCompletion(this, item.insertText, item.detail, item.declaringClass, item.visibility, item.isStatic, item.isFinal, item.isDeprecated, isBlocksContext));
				}
			}
		}
	}

	private static synchronized Map<String, List<String>> getImportTreeCached(Workspace workspace) {
		long now = System.currentTimeMillis();
		if (cachedImportTree == null || (now - lastImportTreeUpdate > 5000)) {
			Map<String, List<String>> tree = new HashMap<>();
			if (workspace != null && workspace.getGenerator() != null) {
				if (workspace.getGenerator().getGradleCache() != null) {
					Map<String, List<String>> gTree = workspace.getGenerator().getGradleCache().getImportTree();
					if (gTree != null) {
						for (Map.Entry<String, List<String>> e : gTree.entrySet()) {
							tree.put(e.getKey(), new ArrayList<>(e.getValue()));
						}
					}
				} else if (workspace.getGenerator().getProjectJarManager() != null) {
					Map<String, List<String>> jTree = ImportTreeBuilder.generateImportTree(workspace.getGenerator().getProjectJarManager());
					if (jTree != null) {
						for (Map.Entry<String, List<String>> e : jTree.entrySet()) {
							tree.put(e.getKey(), new ArrayList<>(e.getValue()));
						}
					}
				}
				ImportTreeBuilder.reloadClassesFromMod(workspace.getGenerator(), tree);
			}
			cachedImportTree = tree;
			lastImportTreeUpdate = now;
		}
		return cachedImportTree;
	}

	private boolean matchesFilter(String candidate, String filter) {
		if (filter == null || filter.isEmpty()) return true;
		return candidate.toLowerCase(Locale.ROOT).startsWith(filter.toLowerCase(Locale.ROOT));
	}

	private static String extractTargetName(String beforeDot) {
		String targetName = "";
		int depth = 0;
		for (int i = beforeDot.length() - 1; i >= 0; i--) {
			char c = beforeDot.charAt(i);
			if (c == ')') depth++;
			else if (c == '(') depth--;
			else if (depth == 0) {
				if (!Character.isLetterOrDigit(c) && c != '_' && c != '.') {
					break;
				}
			}
			targetName = c + targetName;
		}
		return targetName.trim();
	}
}