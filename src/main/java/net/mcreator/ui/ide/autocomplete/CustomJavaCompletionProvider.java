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
import org.fife.rsta.ac.java.DecoratableIcon;
import org.fife.rsta.ac.java.IconFactory;
import org.fife.ui.autocomplete.*;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomJavaCompletionProvider extends DefaultCompletionProvider {

	private static final Pattern DOC_WORD_PATTERN = Pattern.compile("\\b([a-zA-Z_][a-zA-Z0-9_]*)\\b");
	private static final Pattern CLASS_CONTEXT_PATTERN = Pattern.compile("(?:new|extends|implements|import|class|interface|enum)\\s*$");

	private final Workspace workspace;
	@Nullable private final StringCompletitionProvider stringProvider;

	private static Map<String, List<String>> cachedImportTree = null;
	private static long lastImportTreeUpdate = 0;

	private static class ClassInfo {
		final String pkg;
		final boolean isInterface;
		final boolean isEnum;

		ClassInfo(String fqdn) {
			this.pkg = fqdn.contains(".") ? fqdn.substring(0, fqdn.lastIndexOf('.')) : "";
			boolean inf = false;
			boolean enm = false;
			try {
				Class<?> clazz = Class.forName(fqdn);
				inf = clazz.isInterface();
				enm = clazz.isEnum();
			} catch (Throwable ignored) {
			}
			this.isInterface = inf;
			this.isEnum = enm;
		}
	}

	private static final Map<String, ClassInfo> CLASS_INFO_CACHE = new ConcurrentHashMap<>();

	private static ClassInfo getClassInfo(String fqdn) {
		return CLASS_INFO_CACHE.computeIfAbsent(fqdn, ClassInfo::new);
	}

	public static class CustomMethodCompletion extends TemplateCompletion {
		private final String name;
		private final String returnType;
		private final String declaringClass;
		private final String docSummary;
		private final String visibility;
		private final boolean isStatic;
		private final boolean isAbstract;
		private final boolean isDeprecated;

		public CustomMethodCompletion(CompletionProvider provider, String name, String returnType, String declaringClass, String template, String docSummary, String visibility, boolean isStatic, boolean isAbstract, boolean isDeprecated) {
			super(provider, name, name, template, null, null);
			this.name = name;
			this.returnType = returnType;
			this.declaringClass = declaringClass;
			this.docSummary = docSummary;
			this.visibility = visibility;
			this.isStatic = isStatic;
			this.isAbstract = isAbstract;
			this.isDeprecated = isDeprecated;
			setRelevance(100);
		}

		@Override
		public Icon getIcon() {
			String iconKey = switch (visibility != null ? visibility : "public") {
				case "protected" -> IconFactory.METHOD_PROTECTED_ICON;
				case "private" -> IconFactory.METHOD_PRIVATE_ICON;
				case "package" -> IconFactory.METHOD_DEFAULT_ICON;
				default -> IconFactory.METHOD_PUBLIC_ICON;
			};
			Icon baseIcon = IconFactory.get().getIcon(iconKey);
			if (isStatic || isAbstract || isDeprecated) {
				DecoratableIcon dec = new DecoratableIcon(baseIcon);
				if (isDeprecated) {
					dec.addDecorationIcon(IconFactory.get().getIcon(IconFactory.DEPRECATED_ICON));
				}
				if (isAbstract) {
					dec.addDecorationIcon(IconFactory.get().getIcon(IconFactory.ABSTRACT_ICON));
				}
				if (isStatic) {
					dec.addDecorationIcon(IconFactory.get().getIcon(IconFactory.STATIC_ICON));
				}
				return dec;
			}
			return baseIcon;
		}

		@Override
		public String getSummary() {
			if (docSummary == null) return null;
			String safeDoc = docSummary.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
			return "<html>" + safeDoc + "</html>";
		}

		@Override
		public String toString() {
			String label = name;
			if (docSummary != null && docSummary.contains("(") && docSummary.contains(")")) {
				int start = docSummary.indexOf('(');
				int end = docSummary.indexOf(')', start);
				if (start != -1 && end != -1) {
					String paramsPart = docSummary.substring(start, end + 1);
					paramsPart = paramsPart.replaceAll("[a-zA-Z0-9_.]+\\.([a-zA-Z0-9_]+)", "$1");
					label = name + paramsPart;
				}
			}
			String text = label + " : " + returnType;
			if (declaringClass != null && !declaringClass.isEmpty()) {
				text += " - " + declaringClass;
			}
			return text;
		}
	}

	public static class CustomFieldCompletion extends VariableCompletion {
		private final String name;
		private final String type;
		private final String declaringClass;
		private final String visibility;
		private final boolean isStatic;
		private final boolean isFinal;
		private final boolean isDeprecated;

		public CustomFieldCompletion(CompletionProvider provider, String name, String type, String declaringClass, String visibility, boolean isStatic, boolean isFinal, boolean isDeprecated, boolean isBlocksContext) {
			super(provider, isBlocksContext ? "Blocks." + name : name, type);
			this.name = name;
			this.type = type;
			this.declaringClass = declaringClass;
			this.visibility = visibility;
			this.isStatic = isStatic;
			this.isFinal = isFinal;
			this.isDeprecated = isDeprecated;
			setRelevance(100);
		}

		@Override
		public Icon getIcon() {
			String iconKey = switch (visibility != null ? visibility : "public") {
				case "protected" -> IconFactory.FIELD_PROTECTED_ICON;
				case "private" -> IconFactory.FIELD_PRIVATE_ICON;
				case "package" -> IconFactory.FIELD_DEFAULT_ICON;
				default -> IconFactory.FIELD_PUBLIC_ICON;
			};
			Icon baseIcon = IconFactory.get().getIcon(iconKey);
			if (isStatic || isFinal || isDeprecated) {
				DecoratableIcon dec = new DecoratableIcon(baseIcon);
				if (isDeprecated) {
					dec.addDecorationIcon(IconFactory.get().getIcon(IconFactory.DEPRECATED_ICON));
				}
				if (isStatic) {
					dec.addDecorationIcon(IconFactory.get().getIcon(IconFactory.STATIC_ICON));
				}
				if (isFinal) {
					dec.addDecorationIcon(IconFactory.get().getIcon(IconFactory.FINAL_ICON));
				}
				return dec;
			}
			return baseIcon;
		}

		@Override
		public String getSummary() {
			String safeType = type != null ? type.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") : "";
			String safeName = name != null ? name.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") : "";
			return "<html>" + safeType + " " + safeName + "</html>";
		}

		@Override
		public String toString() {
			String text = name + " : " + type;
			if (declaringClass != null && !declaringClass.isEmpty()) {
				text += " - " + declaringClass;
			}
			return text;
		}
	}

	public static class CustomClassCompletion extends BasicCompletion {
		private final String className;
		private final String pkg;
		private final boolean isInterface;
		private final boolean isEnum;

		public CustomClassCompletion(CompletionProvider provider, String name, String pkg, boolean isInterface, boolean isEnum) {
			super(provider, name, pkg, (isInterface ? "interface " : (isEnum ? "enum " : "class ")) + name + (pkg != null && !pkg.isEmpty() ? "\npackage " + pkg : ""));
			this.className = name;
			this.pkg = pkg;
			this.isInterface = isInterface;
			this.isEnum = isEnum;
			setRelevance(2);
		}

		@Override
		public Icon getIcon() {
			if (isInterface) {
				return IconFactory.get().getIcon(IconFactory.INTERFACE_ICON);
			} else if (isEnum) {
				return IconFactory.get().getIcon(IconFactory.ENUM_ICON);
			}
			return IconFactory.get().getIcon(IconFactory.CLASS_ICON);
		}

		@Override
		public String toString() {
			if (pkg != null && !pkg.isEmpty()) {
				return className + " - " + pkg;
			}
			return className;
		}
	}

	public static class CustomVariableCompletion extends BasicCompletion {
		public CustomVariableCompletion(CompletionProvider provider, String name) {
			super(provider, name, "Local symbol", name);
			setRelevance(1);
		}

		@Override
		public Icon getIcon() {
			return IconFactory.get().getIcon(IconFactory.LOCAL_VARIABLE_ICON);
		}
	}

	public CustomJavaCompletionProvider(Workspace workspace) {
		this.workspace = workspace;
		this.stringProvider = workspace != null ? new StringCompletitionProvider(workspace) : null;
		setAutoActivationRules(true, ".");
	}

	@Override
	public String getAlreadyEnteredText(JTextComponent comp) {
		Document doc = comp.getDocument();
		int dot = comp.getCaretPosition();
		Element root = doc.getDefaultRootElement();
		int index = root.getElementIndex(dot);
		Element elem = root.getElement(index);
		int start = elem.getStartOffset();
		try {
			String line = doc.getText(start, dot - start);
			int len = line.length();
			int wordStart = len;
			while (wordStart > 0 && Character.isJavaIdentifierPart(line.charAt(wordStart - 1))) {
				wordStart--;
			}
			String word = line.substring(wordStart);
			String prefix = line.substring(0, wordStart).trim();
			if (prefix.endsWith("Blocks.")) {
				return "Blocks." + word;
			} else if (prefix.endsWith("Items.")) {
				return "Items." + word;
			}
			return word;
		} catch (BadLocationException e) {
			return "";
		}
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
				for (JavaTypeResolver.CompletionItem item : items) {
					String methodName = item.label.contains("(") ? item.label.substring(0, item.label.indexOf('(')) : item.label;
					if (matchesFilter(methodName, wordOnly) || (item.kind.equals("field") && matchesFilter(item.insertText, wordOnly))) {
						if (item.kind.equals("method")) {
							String template = item.insertText.replaceAll("\\$\\{\\d+:", "\\${");
							if (!template.contains("${")) {
								template = template + "${cursor}";
							}

							StringBuilder docSb = new StringBuilder();
							docSb.append(item.detail).append(" ").append(methodName).append("(");
							if (item.paramNames != null && !item.paramNames.isEmpty()) {
								for (int p = 0; p < item.paramNames.size(); p++) {
									if (p > 0) docSb.append(", ");
									String pType = item.paramTypes != null && p < item.paramTypes.size() ? item.paramTypes.get(p) : "Object";
									String pName = item.paramNames.get(p);
									docSb.append(pType).append(" ").append(pName);
								}
							}
							docSb.append(")");

							CustomMethodCompletion mc = new CustomMethodCompletion(this, methodName, item.detail, item.declaringClass, template, docSb.toString(), item.visibility, item.isStatic, item.isAbstract, item.isDeprecated);
							completions.add(mc);
						} else {
							CustomFieldCompletion fc = new CustomFieldCompletion(this, item.insertText, item.detail, item.declaringClass, item.visibility, item.isStatic, item.isFinal, item.isDeprecated, alreadyEntered.startsWith("Blocks.") || alreadyEntered.startsWith("Items."));
							completions.add(fc);
						}
					}
				}
			}
		} else {
			// General completions
			List<JavaTypeResolver.CompletionItem> thisItems = JavaTypeResolver.getCompletionsFor("this", code, codeBeforeCursor, workspace);
			for (JavaTypeResolver.CompletionItem item : thisItems) {
				String methodName = item.label.contains("(") ? item.label.substring(0, item.label.indexOf('(')) : item.label;
				if (matchesFilter(methodName, wordOnly) || (item.kind.equals("field") && matchesFilter(item.insertText, wordOnly))) {
					if (item.kind.equals("method")) {
						String template = item.insertText.replaceAll("\\$\\{\\d+:", "\\${");
						if (!template.contains("${")) {
							template = template + "${cursor}";
						}

						StringBuilder docSb = new StringBuilder();
						docSb.append(item.detail).append(" ").append(methodName).append("(");
						if (item.paramNames != null && !item.paramNames.isEmpty()) {
							for (int p = 0; p < item.paramNames.size(); p++) {
								if (p > 0) docSb.append(", ");
								String pType = item.paramTypes != null && p < item.paramTypes.size() ? item.paramTypes.get(p) : "Object";
								String pName = item.paramNames.get(p);
								docSb.append(pType).append(" ").append(pName);
							}
						}
						docSb.append(")");

						CustomMethodCompletion mc = new CustomMethodCompletion(this, methodName, item.detail, item.declaringClass, template, docSb.toString(), item.visibility, item.isStatic, item.isAbstract, item.isDeprecated);
						completions.add(mc);
					} else {
						CustomFieldCompletion fc = new CustomFieldCompletion(this, item.insertText, item.detail, item.declaringClass, item.visibility, item.isStatic, item.isFinal, item.isDeprecated, false);
						completions.add(fc);
					}
				}
			}

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

			// Document words
			Set<String> addedWords = new HashSet<>(JavaConventions.JAVA_RESERVED_WORDS);
			Matcher docWordMatcher = DOC_WORD_PATTERN.matcher(code);
			while (docWordMatcher.find()) {
				String w = docWordMatcher.group(1);
				if (w.length() > 1 && addedWords.add(w)) {
					if (matchesFilter(w, wordOnly)) {
						CustomVariableCompletion cvc = new CustomVariableCompletion(this, w);
						completions.add(cvc);
					}
				}
			}

			// External classes & workspace classes
			boolean isClassContext = CLASS_CONTEXT_PATTERN.matcher(textBeforeWord.trim()).find() ||
					textBeforeWord.trim().endsWith("@") ||
					(!wordOnly.isEmpty() && Character.isUpperCase(wordOnly.charAt(0)));

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
