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
import net.mcreator.java.ProjectJarManager;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.rsta.ac.java.JavaParser;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.ui.autocomplete.*;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.Token;

import org.jboss.forge.roaster.model.source.*;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomJavaCompletionProvider extends DefaultCompletionProvider {

	private static final Logger LOG = LogManager.getLogger(CustomJavaCompletionProvider.class);

	private static final ExecutorService COMPLETION_EXECUTOR = Executors.newFixedThreadPool(
			Math.max(2, Runtime.getRuntime().availableProcessors()), r -> {
				Thread t = new Thread(r, "CustomJavaCompletionProvider-Worker");
				t.setDaemon(true);
				return t;
			});

	private final Workspace workspace;
	private final JavaParser parser;
	private final JavaTypeResolver javaTypeResolver;
	
	private Map<String, List<String>> cachedModClasses = null;
	private long lastModClassesUpdate = 0;

	private record ClassInfo(String pkg, boolean isInterface, boolean isEnum) {
		static ClassInfo of(Workspace workspace, String fqdn) {
			String pkg = fqdn.contains(".") ? fqdn.substring(0, fqdn.lastIndexOf('.')) : "";
			boolean inf = false, enm = false;
			if (workspace != null) {
				ProjectJarManager jarManager = workspace.getGenerator().getProjectJarManager();
				try {
					ClassFile cf = jarManager.getClassEntry(fqdn);
					if (cf != null) {
						int flags = cf.getAccessFlags();
						inf = (flags & 0x0200) != 0;
						enm = (flags & 0x4000) != 0;
					}
				} catch (Throwable e) {
					LOG.debug("Failed to read class file entry for {}", fqdn, e);
				}
			}
			return new ClassInfo(pkg, inf, enm);
		}
	}

	private record ScopeBlock(StringBuilder text, int headerStartInParent) {
		ScopeBlock(int headerStartInParent) {
			this(new StringBuilder(), headerStartInParent);
		}
	}

	private final Map<String, ClassInfo> classInfoCache = new ConcurrentHashMap<>();

	private ClassInfo getClassInfo(String fqdn) {
		return classInfoCache.computeIfAbsent(fqdn, _ -> ClassInfo.of(workspace, fqdn));
	}

	public CustomJavaCompletionProvider(Workspace workspace, JavaParser parser) {
		this.workspace = workspace;
		this.parser = parser;
		this.javaTypeResolver = new JavaTypeResolver(workspace);
		setAutoActivationRules(!"Trigger on dot".equals(PreferencesManager.PREFERENCES.ide.autocompleteMode.get()),
				".");
		setParameterizedCompletionParams('(', ", ", ')');
	}

	public synchronized void invalidateCaches() {
		classInfoCache.clear();
		cachedModClasses = null;
		lastModClassesUpdate = 0;
		if (javaTypeResolver != null) {
			javaTypeResolver.invalidateCaches();
		}
	}

	private boolean isInsideCommentOrString(JTextComponent comp) {
		if (!(comp instanceof RSyntaxTextArea rsta))
			return false;
		RSyntaxDocument doc = (RSyntaxDocument) rsta.getDocument();
		int line = rsta.getCaretLineNumber();
		Token t = doc.getTokenListForLine(line);
		if (t == null)
			return false;
		int dot = rsta.getCaretPosition();
		Token curToken = RSyntaxUtilities.getTokenAtOffset(t, dot);
		int type;
		if (curToken != null && dot > curToken.getOffset()) {
			type = curToken.getType();
		} else {
			type = doc.getLastTokenTypeOnLine(line);
			if (type == Token.NULL && t.getLastPaintableToken() != null) {
				type = t.getLastPaintableToken().getType();
			} else if ((type == Token.NULL || (curToken != null && dot == curToken.getOffset())) && line > 0) {
				type = doc.getLastTokenTypeOnLine(line - 1);
			}
			if (type < 0) {
				type = doc.getClosestStandardTokenTypeForInternalType(type);
			}
		}
		return (type >= Token.COMMENT_EOL && type <= Token.COMMENT_MARKUP) || type == Token.LITERAL_STRING_DOUBLE_QUOTE
				|| type == Token.ERROR_STRING_DOUBLE || type == Token.LITERAL_CHAR || type == Token.ERROR_CHAR
				|| type == Token.LITERAL_BACKQUOTE;
	}

	@Override public boolean isAutoActivateOkay(JTextComponent tc) {
		if (isInsideCommentOrString(tc))
			return false;
		return super.isAutoActivateOkay(tc);
	}

	@Override public String getAlreadyEnteredText(JTextComponent comp) {
		if (isInsideCommentOrString(comp))
			return "";
		String word = super.getAlreadyEnteredText(comp);
		if (word == null)
			word = "";
		try {
			int caret = comp.getCaretPosition();
			String textBefore = comp.getText(0, Math.max(0, caret - word.length())).trim();
			if (textBefore.endsWith("Blocks."))
				return "Blocks." + word;
			if (textBefore.endsWith("Items."))
				return "Items." + word;
		} catch (BadLocationException e) {
			LOG.debug("Failed to get already entered text", e);
		}
		return word;
	}

	@Override protected List<Completion> getCompletionsImpl(JTextComponent comp) {
		List<Completion> completions = new ArrayList<>();
		if (!PreferencesManager.PREFERENCES.ide.autocomplete.get() || isInsideCommentOrString(comp)) {
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
		String wordOnly = alreadyEntered.contains(".") ?
				alreadyEntered.substring(alreadyEntered.lastIndexOf('.') + 1) :
				alreadyEntered;

		String textBeforeWord = lineUntilPosition.substring(0,
				Math.max(0, lineUntilPosition.length() - wordOnly.length()));
		boolean isDotContext = textBeforeWord.trim().endsWith(".");

		if (isDotContext) {
			String beforeDot = textBeforeWord.substring(0, textBeforeWord.lastIndexOf('.')).trim();
			String targetName = extractTargetName(beforeDot);
			if (!targetName.isEmpty()) {
				List<JavaTypeResolver.CompletionItem> items = javaTypeResolver.getCompletionsFor(targetName, code,
						codeBeforeCursor, parser);
				CustomFieldCompletion.PrefixContext prefixContext = CustomFieldCompletion.PrefixContext.NONE;
				if (alreadyEntered.startsWith("Blocks.")) {
					prefixContext = CustomFieldCompletion.PrefixContext.BLOCKS;
				} else if (alreadyEntered.startsWith("Items.")) {
					prefixContext = CustomFieldCompletion.PrefixContext.ITEMS;
				}
				addResolverItems(items, wordOnly, prefixContext, completions);
			}
		} else {
			// Method/field completions for "this" - executed synchronously
			List<JavaTypeResolver.CompletionItem> thisItems = javaTypeResolver.getCompletionsFor("this", code,
					codeBeforeCursor, parser);
			addResolverItems(thisItems, wordOnly, CustomFieldCompletion.PrefixContext.NONE, completions);

			// Keywords - executed synchronously
			for (String kw : JavaConventions.JAVA_RESERVED_WORDS) {
				if (matchesFilter(kw, wordOnly)) {
					completions.add(new JavaKeywordCompletion(this, kw));
				}
			}

			// Local symbols (method parameters and local variables in current method scope)
			Map<String, String> localVars = getLocalVariables(codeBeforeCursor);
			for (Map.Entry<String, String> entry : localVars.entrySet()) {
				String varName = entry.getKey();
				if (matchesFilter(varName, wordOnly) && !varName.equals(wordOnly)) {
					completions.add(new CustomVariableCompletion(this, varName, entry.getValue()));
				}
			}

			// External classes & workspace classes - offloaded to background thread with timeout
			String trimmedBefore = textBeforeWord.trim();
			boolean isClassContext =
					trimmedBefore.endsWith("@") || (!wordOnly.isEmpty() && Character.isUpperCase(wordOnly.charAt(0)));
			if (!isClassContext) {
				for (String kw : new String[] { "new", "extends", "implements", "import", "class", "interface",
						"enum" }) {
					if (trimmedBefore.endsWith(kw)) {
						isClassContext = true;
						break;
					}
				}
			}

			if (isClassContext && workspace != null) {
				if ("Smart".equals(mode)) {
					CompletableFuture<List<Completion>> classTask = CompletableFuture.supplyAsync(() -> {
						List<Completion> classComps = new ArrayList<>();
						addClassCompletions(wordOnly, classComps);
						return classComps;
					}, COMPLETION_EXECUTOR);

					try {
						completions.addAll(classTask.get(100, TimeUnit.MILLISECONDS));
					} catch (TimeoutException e) {
						// Class completions timed out in smart mode; return method/field/keyword completions immediately
					} catch (Exception e) {
						LOG.error("Failed to get class completions", e);
					}
				} else {
					// Autocomplete is not in smart mode: run class completion synchronously so completions are always shown
					addClassCompletions(wordOnly, completions);
				}
			}
		}

		List<Completion> result = new ArrayList<>(completions);
		result.sort((c1, c2) -> {
			int r1 = c1.getRelevance();
			int r2 = c2.getRelevance();
			if (r1 != r2)
				return Integer.compare(r2, r1);
			int cmp = c1.toString().compareTo(c2.toString());
			if (cmp != 0)
				return cmp;
			return Integer.compare(System.identityHashCode(c1), System.identityHashCode(c2));
		});

		return result;
	}

	private void addClassCompletions(String wordOnly, List<Completion> completions) {
		if (workspace == null)
			return;
		Map<String, List<String>> tree = getImportTreeCached();
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
								CustomClassCompletion ccc = new CustomClassCompletion(this, className, info.pkg,
										info.isInterface, info.isEnum);
								completions.add(ccc);
							}
						}
					}
				}
			}
		}
	}

	private void addResolverItems(List<JavaTypeResolver.CompletionItem> items, String wordOnly,
			CustomFieldCompletion.PrefixContext prefixContext, List<Completion> completions) {
		for (JavaTypeResolver.CompletionItem item : items) {
			String methodName = item.label().contains("(") ?
					item.label().substring(0, item.label().indexOf('(')) :
					item.label();
			if (matchesFilter(methodName, wordOnly) || (item.kind().equals("field") && matchesFilter(item.insertText(),
					wordOnly))) {
				if (item.kind().equals("method")) {
					String template = item.insertText().replaceAll("\\$\\{\\d+:", "\\${");
					if (!template.contains("${")) {
						template = template + "${cursor}";
					}

					String docStr = item.docSummary() != null ? item.docSummary() : item.detail() + " " + item.label();

					completions.add(new CustomMethodCompletion(this, methodName, item.label(), item.detail(),
							item.declaringClass(), template, docStr, item.visibility(), item.isStatic(),
							item.isAbstract(), item.isDeprecated(), item.paramTypes(), item.paramNames()));
				} else {
					completions.add(
							new CustomFieldCompletion(this, item.insertText(), item.detail(), item.declaringClass(),
									item.visibility(), item.isStatic(), item.isFinal(), item.isDeprecated(),
									prefixContext));
				}
			}
		}
	}

	private synchronized Map<String, List<String>> getImportTreeCached() {
		if (workspace == null || workspace.getGenerator().getGradleCache() == null)
			return null;

		Map<String, List<String>> gTree = workspace.getGenerator().getGradleCache().getImportTree();
		if (gTree == null)
			return null;

		Map<String, List<String>> tree = new HashMap<>();
		for (Map.Entry<String, List<String>> e : gTree.entrySet()) {
			tree.put(e.getKey(), new ArrayList<>(e.getValue()));
		}

		long now = System.currentTimeMillis();
		if (cachedModClasses == null || (now - lastModClassesUpdate > 5000)) {
			Map<String, List<String>> modClasses = new HashMap<>();
			ImportTreeBuilder.reloadClassesFromMod(workspace.getGenerator(), modClasses);
			cachedModClasses = modClasses;
			lastModClassesUpdate = now;
		}

		for (Map.Entry<String, List<String>> e : cachedModClasses.entrySet()) {
			tree.computeIfAbsent(e.getKey(), _ -> new ArrayList<>()).addAll(e.getValue());
		}

		return tree;
	}

	private boolean matchesFilter(String candidate, String filter) {
		if (filter == null || filter.isEmpty())
			return true;
		return candidate.toLowerCase(Locale.ROOT).startsWith(filter.toLowerCase(Locale.ROOT));
	}

	private static String extractTargetName(String beforeDot) {
		String targetName = "";
		int depth = 0;
		for (int i = beforeDot.length() - 1; i >= 0; i--) {
			char c = beforeDot.charAt(i);
			if (c == ')') {
				depth++;
			} else if (c == '(') {
				if (depth > 0) {
					depth--;
				} else {
					break;
				}
			} else if (depth == 0) {
				if (!Character.isLetterOrDigit(c) && c != '_' && c != '.') {
					break;
				}
			}
			targetName = c + targetName;
		}
		return targetName.trim();
	}

	private int findHeaderStart(StringBuilder sb) {
		int depth = 0;
		for (int j = sb.length() - 1; j >= 0; j--) {
			char c = sb.charAt(j);
			if (c == ')') {
				depth++;
			} else if (c == '(') {
				if (depth > 0) {
					depth--;
				}
			} else if (depth == 0) {
				if (c == ';' || c == '{' || c == '}') {
					return j + 1;
				}
			}
		}
		return 0;
	}

	private Map<String, String> getLocalVariables(String codeBeforeCursor) {
		Map<String, String> vars = new LinkedHashMap<>();
		if (codeBeforeCursor == null || codeBeforeCursor.isEmpty())
			return vars;

		String strippedCode = LocalVariableResolver.stripCommentsAndStrings(codeBeforeCursor);

		Deque<ScopeBlock> stack = new ArrayDeque<>();
		stack.push(new ScopeBlock(0));

		for (int i = 0; i < strippedCode.length(); i++) {
			char c = strippedCode.charAt(i);
			if (c == '{') {
				int headerStart = 0;
				if (stack.peek() != null) {
					headerStart = findHeaderStart(stack.peek().text);
				}
				stack.push(new ScopeBlock(headerStart));
			} else if (c == '}') {
				if (stack.size() > 1) {
					ScopeBlock closed = stack.pop();
					if (stack.peek() != null) {
						stack.peek().text.setLength(closed.headerStartInParent);
					}
				}
			} else {
				if (stack.peek() != null) {
					stack.peek().text.append(c);
				}
			}
		}

		StringBuilder activeCode = new StringBuilder();
		for (ScopeBlock block : stack) {
			activeCode.append(block.text).append('\n');
		}

		Matcher m = Pattern.compile(
						"\\b(boolean|byte|char|short|int|long|float|double|[A-Z][A-Za-z0-9_.]*(?:<[^>]+>)?(?:\\[])*)\\s+([a-zA-Z_$][a-zA-Z0-9_$]*)\\b")
				.matcher(activeCode);
		while (m.find()) {
			String type = m.group(1);
			String name = m.group(2);
			if (type.contains("."))
				type = type.substring(type.lastIndexOf('.') + 1);
			if (!JavaConventions.JAVA_RESERVED_WORDS.contains(name)) {
				vars.putIfAbsent(name, type);
			}
		}

		return vars;
	}
}