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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.java.ProjectJarManager;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.rsta.ac.ShorthandCompletionCache;
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
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class CustomJavaCompletionProvider extends DefaultCompletionProvider {

	private static final Logger LOG = LogManager.getLogger(CustomJavaCompletionProvider.class);

	private static final int COMPLETION_TIMEOUT_MS = 25;

	private static final ExecutorService COMPLETION_EXECUTOR = Executors.newFixedThreadPool(
			Math.max(2, Runtime.getRuntime().availableProcessors()), r -> {
				Thread t = new Thread(r, "CustomJavaCompletionProvider-Worker");
				t.setDaemon(true);
				return t;
			});

	private final Workspace workspace;
	private final JavaParser parser;
	private final JavaTypeResolver javaTypeResolver;
	private final ShorthandCompletionCache shorthandCache;
	private AutoCompletion ac;

	private record CompletionCacheKey(Document doc, int docLength, int caretPos, String alreadyEntered) {}

	private final AtomicLong currentRequestId = new AtomicLong(0);
	private volatile boolean cancelledByUser = false;

	@SuppressWarnings("NullableProblems")
	private final Cache<CompletionCacheKey, List<Completion>> computedCompletionsCache = CacheBuilder.newBuilder()
			.maximumSize(50).expireAfterWrite(Duration.ofSeconds(5)).build();

	@SuppressWarnings("NullableProblems")
	private final Cache<String, List<Completion>> classCompletionsCache = CacheBuilder.newBuilder().maximumSize(100)
			.expireAfterWrite(Duration.ofSeconds(10)).build();

	private record ClassInfo(String pkg, boolean isInterface, boolean isEnum) {
		static ClassInfo of(Workspace workspace, String fqdn) {
			String pkg = fqdn.contains(".") ? fqdn.substring(0, fqdn.lastIndexOf('.')) : "";
			boolean inf = false, enm = false;
			if (workspace != null) {
				ProjectJarManager jarManager = workspace.getGenerator().getProjectJarManager();
				try {
					ClassFile cf = null;
					if (jarManager != null) {
						cf = jarManager.getClassEntry(fqdn);
					}
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

	private final Map<String, ClassInfo> classInfoCache = new ConcurrentHashMap<>();

	private ClassInfo getClassInfo(String fqdn) {
		return classInfoCache.computeIfAbsent(fqdn, _ -> ClassInfo.of(workspace, fqdn));
	}

	public CustomJavaCompletionProvider(Workspace workspace, JavaParser parser) {
		this.workspace = workspace;
		this.parser = parser;
		this.javaTypeResolver = new JavaTypeResolver(workspace);
		this.shorthandCache = new CustomJSCCache(this, new DefaultCompletionProvider());
		setAutoActivationRules(!"Trigger on dot".equals(PreferencesManager.PREFERENCES.ide.autocompleteMode.get()),
				".");
		setParameterizedCompletionParams('(', ", ", ')');
	}

	public void setAc(AutoCompletion ac) {
		this.ac = ac;
	}

	public void cancelPendingCompletion() {
		currentRequestId.incrementAndGet();
		cancelledByUser = true;
	}

	public synchronized void invalidateCaches() {
		cancelPendingCompletion();
		classInfoCache.clear();
		classCompletionsCache.invalidateAll();
		computedCompletionsCache.invalidateAll();
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
		if (!PreferencesManager.PREFERENCES.ide.autocomplete.get() || isInsideCommentOrString(comp)) {
			return Collections.emptyList();
		}

		Document doc = comp.getDocument();
		int caretPos = comp.getCaretPosition();
		int docLength = doc.getLength();
		String alreadyEntered = getAlreadyEnteredText(comp);

		CompletionCacheKey cacheKey = new CompletionCacheKey(doc, docLength, caretPos, alreadyEntered);
		List<Completion> cached = computedCompletionsCache.getIfPresent(cacheKey);
		if (cached != null) {
			return cached;
		}

		String code;
		try {
			code = doc.getText(0, docLength);
		} catch (BadLocationException e) {
			return Collections.emptyList();
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

		String wordOnly = alreadyEntered.contains(".") ?
				alreadyEntered.substring(alreadyEntered.lastIndexOf('.') + 1) :
				alreadyEntered;
		String textBeforeWord = lineUntilPosition.substring(0,
				Math.max(0, lineUntilPosition.length() - wordOnly.length()));
		boolean isDotContext = textBeforeWord.trim().endsWith(".");

		cancelledByUser = false;
		long requestId = currentRequestId.incrementAndGet();
		final AtomicBoolean timedOut = new AtomicBoolean(false);

		CompletableFuture<List<Completion>> future = CompletableFuture.supplyAsync(
				() -> computeCompletions(code, codeBeforeCursor, alreadyEntered, wordOnly, textBeforeWord,
						isDotContext), COMPLETION_EXECUTOR);

		future.whenComplete((result, ex) -> {
			if (ex != null) {
				LOG.error("Failed to compute completions asynchronously", ex);
				return;
			}
			if (result != null) {
				computedCompletionsCache.put(cacheKey, result);
			}
			if (timedOut.get() && requestId == currentRequestId.get() && !cancelledByUser && result != null
					&& !result.isEmpty()) {
				SwingUtilities.invokeLater(() -> {
					try {
						if (requestId == currentRequestId.get() && !cancelledByUser && comp.getDocument() == doc
								&& comp.getCaretPosition() == caretPos && comp.isFocusOwner()) {
							if (ac != null) {
								ac.doCompletion();
							}
						}
					} catch (Throwable t) {
						LOG.debug("Failed to trigger completions after async compute", t);
					}
				});
			}
		});

		try {
			List<Completion> syncComps = future.get(COMPLETION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
			if (syncComps != null) {
				computedCompletionsCache.put(cacheKey, syncComps);
				return syncComps;
			}
		} catch (TimeoutException e) {
			timedOut.set(true);
		} catch (Exception e) {
			LOG.error("Failed to get completions synchronously", e);
		}

		return Collections.emptyList();
	}

	private List<Completion> computeCompletions(String code, String codeBeforeCursor, String alreadyEntered,
			String wordOnly, String textBeforeWord, boolean isDotContext) {
		List<Completion> completions = new ArrayList<>();

		if (isDotContext) {
			String beforeDot = textBeforeWord.substring(0, textBeforeWord.lastIndexOf('.')).trim();
			String targetName = extractTargetName(beforeDot);
			if (!targetName.isEmpty()) {
				CustomFieldCompletion.PrefixContext prefixContext = CustomFieldCompletion.PrefixContext.NONE;
				if (alreadyEntered.startsWith("Blocks.")) {
					prefixContext = CustomFieldCompletion.PrefixContext.BLOCKS;
				} else if (alreadyEntered.startsWith("Items.")) {
					prefixContext = CustomFieldCompletion.PrefixContext.ITEMS;
				}
				List<JavaTypeResolver.CompletionItem> items = javaTypeResolver.getCompletionsFor(targetName, code,
						codeBeforeCursor, parser);
				addResolverItems(items, wordOnly, prefixContext, completions);
			}
		} else {
			// Method/field completions for "this"
			List<JavaTypeResolver.CompletionItem> thisItems = javaTypeResolver.getCompletionsFor("this", code,
					codeBeforeCursor, parser);
			addResolverItems(thisItems, wordOnly, CustomFieldCompletion.PrefixContext.NONE, completions);

			// Shorthand completions (templates and keywords)
			for (Completion c : shorthandCache.getShorthandCompletions()) {
				if (matchesFilter(c.getInputText(), wordOnly)) {
					completions.add(c);
				}
			}

			// Local symbols (method parameters and local variables in current method scope)
			Map<String, String> localVars = LocalVariableResolver.getLocalVariables(codeBeforeCursor);
			for (Map.Entry<String, String> entry : localVars.entrySet()) {
				String varName = entry.getKey();
				if (matchesFilter(varName, wordOnly) && !varName.equals(wordOnly)) {
					completions.add(new CustomVariableCompletion(this, varName, entry.getValue()));
				}
			}

			// External classes & workspace classes
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
				Map<String, String> imports = javaTypeResolver.getSourceResolver().parseImports(code);
				List<Completion> cachedClassComps = classCompletionsCache.getIfPresent(wordOnly);
				if (cachedClassComps != null) {
					completions.addAll(cachedClassComps);
				} else {
					List<Completion> classComps = new ArrayList<>();
					addClassCompletions(wordOnly, classComps, imports);
					classCompletionsCache.put(wordOnly, classComps);
					completions.addAll(classComps);
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

	private void addClassCompletions(String wordOnly, List<Completion> completions, Map<String, String> imports) {
		if (workspace == null)
			return;

		Set<String> addedFQDNs = new HashSet<>();

		if (workspace.getGenerator().getGradleCache() != null) {
			Map<String, List<String>> gTree = workspace.getGenerator().getGradleCache().getImportTree();
			addClassCompletionsFromTree(gTree, wordOnly, addedFQDNs, completions, imports);
		}

		Map<String, List<String>> modClasses = javaTypeResolver.getModClasses();
		if (modClasses != null) {
			addClassCompletionsFromTree(modClasses, wordOnly, addedFQDNs, completions, imports);
		}
	}

	private void addClassCompletionsFromTree(Map<String, List<String>> tree, String wordOnly, Set<String> addedFQDNs,
			List<Completion> completions, Map<String, String> imports) {
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
							if (imports != null && fqdn.equals(imports.get(className))) {
								ccc.setRelevance(3);
							}
							completions.add(ccc);
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

					completions.add(new CustomMethodCompletion(this, methodName, item.label(), item.detail(),
							item.declaringClass(), template, item.docSummary(), item.visibility(), item.isStatic(),
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
}