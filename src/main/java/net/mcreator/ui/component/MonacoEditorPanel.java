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

package net.mcreator.ui.component;

import net.mcreator.java.ImportTreeBuilder;
import net.mcreator.java.JavaTypeResolver;
import net.mcreator.java.ProjectJarManager;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.chromium.WebView;
import net.mcreator.ui.component.util.ThreadUtil;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.workspace.Workspace;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MonacoEditorPanel extends JPanel implements Closeable {

	private final WebView webView;
	private final List<ChangeListener> changeListeners = new CopyOnWriteArrayList<>();
	private Runnable saveRequestListener;

	private volatile boolean isLoaded = false;
	private volatile String cachedCode = "";
	private String currentLanguage = "java";
	private boolean currentReadOnly = false;
	private Workspace workspace;

	public MonacoEditorPanel() {
		this("", "java", false);
	}

	public MonacoEditorPanel(String code, String languageOrExtension, boolean readOnly) {
		super(new BorderLayout());

		this.cachedCode = code != null ? code : "";
		this.currentLanguage = mapToMonacoLanguage(languageOrExtension);
		this.currentReadOnly = readOnly;

		webView = new WebView("http://mcreator/monaco/editor.html", false);
		add(webView, BorderLayout.CENTER);

		webView.addLoadListener(() -> {
			webView.addJavaScriptBridge("javabridge", new MonacoBridge());
			applyInitSettings();
		});
	}

	public void setWorkspaceContext(Workspace workspace) {
		this.workspace = workspace;
		if (isLoaded && workspace != null) {
			sendExternalClassesToMonaco();
		}
	}

	private void sendExternalClassesToMonaco() {
		if (workspace == null) return;
		new Thread(() -> {
			String json = buildExternalClassesJson(workspace);
			executeAsyncJS("setExternalClasses(" + json + ");");
		}, "MonacoExternalClassesLoader").start();
	}

	private static String buildExternalClassesJson(Workspace workspace) {
		if (workspace == null || workspace.getGenerator() == null) return "[]";
		try {
			ProjectJarManager jarManager = workspace.getGenerator().getProjectJarManager();
			Map<String, List<String>> tree = jarManager != null ? ImportTreeBuilder.generateImportTree(jarManager) : null;
			StringBuilder sb = new StringBuilder("[");
			boolean first = true;

			if (tree != null) {
				for (Map.Entry<String, List<String>> entry : tree.entrySet()) {
					String className = entry.getKey();
					List<String> fqdns = entry.getValue();
					if (fqdns != null && !fqdns.isEmpty()) {
						String fqdn = fqdns.get(0);
						String pkg = fqdn.contains(".") ? fqdn.substring(0, fqdn.lastIndexOf('.')) : "";
						if (!first) sb.append(",");
						sb.append("{\"name\":\"").append(className).append("\",\"pkg\":\"").append(pkg).append("\"}");
						first = false;
					}
				}
			}

			File srcRoot = workspace.getGenerator().getSourceRoot();
			if (srcRoot != null && srcRoot.isDirectory()) {
				addWorkspaceSourceFiles(srcRoot, srcRoot, sb, first);
			}

			sb.append("]");
			return sb.toString();
		} catch (Exception e) {
			return "[]";
		}
	}

	private static boolean addWorkspaceSourceFiles(File root, File dir, StringBuilder sb, boolean first) {
		File[] files = dir.listFiles();
		if (files == null) return first;
		for (File f : files) {
			if (f.isDirectory()) {
				first = addWorkspaceSourceFiles(root, f, sb, first);
			} else if (f.getName().endsWith(".java")) {
				String name = f.getName().substring(0, f.getName().length() - 5);
				String relPath = root.toPath().relativize(f.toPath()).toString().replace('\\', '.').replace('/', '.');
				String pkg = relPath.contains(".") ? relPath.substring(0, relPath.lastIndexOf('.')) : "";
				if (pkg.endsWith("." + name)) {
					pkg = pkg.substring(0, pkg.length() - name.length() - 1);
				}
				if (!first) sb.append(",");
				sb.append("{\"name\":\"").append(name).append("\",\"pkg\":\"").append(pkg).append("\"}");
				first = false;
			}
		}
		return first;
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	private void executeAsyncJS(String script) {
		if (webView != null && webView.getBrowser() != null) {
			try {
				webView.getBrowser().executeJavaScript(script, "http://mcreator/monaco/editor.html", 0);
			} catch (Exception ignored) {
			}
		}
	}

	private void applyInitSettings() {
		Color bg = Theme.current().getBackgroundColor();
		boolean isDark = (bg.getRed() * 0.299 + bg.getGreen() * 0.587 + bg.getBlue() * 0.114) < 128;
		String baseTheme = isDark ? "vs-dark" : "vs";
		int fontSize = PreferencesManager.PREFERENCES.ide.fontSize.get();

		boolean autocomplete = PreferencesManager.PREFERENCES.ide.autocomplete.get();
		String autocompleteMode = PreferencesManager.PREFERENCES.ide.autocompleteMode.get();
		boolean autocompleteDocWindow = PreferencesManager.PREFERENCES.ide.autocompleteDocWindow.get();

		String escapedCode = escapeJSString(cachedCode);

		String themeJson = buildMonacoThemeJson(baseTheme);

		String script = "initEditor('" + escapedCode + "', '" + currentLanguage + "', '" + baseTheme + "', " + currentReadOnly + ", " + fontSize + ", " + autocomplete + ", '" + autocompleteMode + "', " + autocompleteDocWindow + ");";
		executeAsyncJS(script);
		executeAsyncJS("applyCustomTheme(" + themeJson + ");");
		isLoaded = true;

		if (workspace != null) {
			sendExternalClassesToMonaco();
		}
	}

	private String buildMonacoThemeJson(String baseTheme) {
		String editorThemePref = PreferencesManager.PREFERENCES.ide.editorTheme.get();

		// Colors from the existing RSyntaxTextArea code_editor.xml themes
		String bgColor, fgColor, caretColor, selectionBg, lineHighlight, lineNumberFg;
		String keywordFg, dataTypeFg, identifierFg, functionFg, annotationFg;
		String commentFg, stringFg, charFg, numberFg, operatorFg, separatorFg, variableFg;

		Color themeBg = Theme.current().getBackgroundColor();
		String accentHex = colorToHex(Theme.current().getInterfaceAccentColor());
		String fgHex = colorToHex(Theme.current().getForegroundColor());

		String themeId = Theme.current().getID();
		Map<String, String> tokens = parseMCreatorThemeXml(themeId);

		bgColor = tokens.getOrDefault("background", colorToHex(themeBg));
		fgColor = fgHex;
		caretColor = tokens.getOrDefault("caret", "c1cbc2");
		selectionBg = tokens.getOrDefault("selection_bg", "404E51");
		lineHighlight = tokens.getOrDefault("currentLineHighlight", "2F393C");
		lineNumberFg = tokens.getOrDefault("lineNumbers_fg", "81969A");

		keywordFg = tokens.getOrDefault("RESERVED_WORD", accentHex);
		dataTypeFg = tokens.getOrDefault("DATA_TYPE", "678CB1");
		identifierFg = tokens.getOrDefault("IDENTIFIER", fgHex);
		functionFg = tokens.getOrDefault("FUNCTION", fgHex);
		annotationFg = tokens.getOrDefault("ANNOTATION", "E8E2B7");
		commentFg = tokens.getOrDefault("COMMENT_EOL", "66747B");
		stringFg = tokens.getOrDefault("LITERAL_STRING_DOUBLE_QUOTE", "00DAFF");
		charFg = tokens.getOrDefault("LITERAL_CHAR", "00DAFF");
		numberFg = tokens.getOrDefault("LITERAL_NUMBER_DECIMAL_INT", accentHex);
		operatorFg = tokens.getOrDefault("OPERATOR", "E8E2B7");
		separatorFg = tokens.getOrDefault("SEPARATOR", "E8E2B7");
		variableFg = tokens.getOrDefault("VARIABLE", "ae9fbf");

		return "{\"base\":\"" + baseTheme + "\"," + "\"bg\":\"" + bgColor + "\"," + "\"fg\":\"" + fgColor + "\","
				+ "\"caret\":\"" + caretColor + "\"," + "\"selectionBg\":\"" + selectionBg + "\","
				+ "\"lineHighlight\":\"" + lineHighlight + "\"," + "\"lineNumberFg\":\"" + lineNumberFg + "\","
				+ "\"keyword\":\"" + keywordFg + "\"," + "\"dataType\":\"" + dataTypeFg + "\"," + "\"identifier\":\""
				+ identifierFg + "\"," + "\"function\":\"" + functionFg + "\"," + "\"annotation\":\"" + annotationFg
				+ "\"," + "\"comment\":\"" + commentFg + "\"," + "\"string\":\"" + stringFg + "\"," + "\"char\":\""
				+ charFg + "\"," + "\"number\":\"" + numberFg + "\"," + "\"operator\":\"" + operatorFg + "\","
				+ "\"separator\":\"" + separatorFg + "\"," + "\"variable\":\"" + variableFg + "\"" + "}";
	}

	private Map<String, String> parseMCreatorThemeXml(String themeId) {
		Map<String, String> result = new HashMap<>();
		try {
			InputStream is = PluginLoader.INSTANCE.getResourceAsStream(
					"themes/" + themeId + "/styles/code_editor.xml");
			if (is == null) {
				is = PluginLoader.INSTANCE.getResourceAsStream(
						"themes/default_dark/styles/code_editor.xml");
			}
			if (is != null) {
				String xml = new String(is.readAllBytes(), StandardCharsets.UTF_8);
				is.close();

				String accentHex = colorToHex(Theme.current().getInterfaceAccentColor());
				String bgHex = colorToHex(Theme.current().getBackgroundColor());
				String fgHex = colorToHex(Theme.current().getForegroundColor());
				String altBgHex = colorToHex(Theme.current().getAltBackgroundColor());
				String altFgHex = colorToHex(Theme.current().getAltForegroundColor());
				String secAltBgHex = colorToHex(Theme.current().getSecondAltBackgroundColor());

				xml = xml.replace("${mainTint}", accentHex)
						.replace("${backgroundColor}", bgHex)
						.replace("${foregroundColor}", fgHex)
						.replace("${altBackgroundColor}", altBgHex)
						.replace("${altForegroundColor}", altFgHex)
						.replace("${secondAltBackgroundColor}", secAltBgHex);

				Matcher bgMatcher = Pattern.compile("<background\\s+color=\"([^\"]+)\"").matcher(xml);
				if (bgMatcher.find()) result.put("background", bgMatcher.group(1));

				Matcher caretMatcher = Pattern.compile("<caret\\s+color=\"([^\"]+)\"").matcher(xml);
				if (caretMatcher.find()) result.put("caret", caretMatcher.group(1));

				Matcher selMatcher = Pattern.compile("<selection[^>]*bg=\"([^\"]+)\"").matcher(xml);
				if (selMatcher.find()) result.put("selection_bg", selMatcher.group(1));

				Matcher clhMatcher = Pattern.compile("<currentLineHighlight\\s+color=\"([^\"]+)\"").matcher(xml);
				if (clhMatcher.find()) result.put("currentLineHighlight", clhMatcher.group(1));

				Matcher lnMatcher = Pattern.compile("<lineNumbers\\s+fg=\"([^\"]+)\"").matcher(xml);
				if (lnMatcher.find()) result.put("lineNumbers_fg", lnMatcher.group(1));

				Pattern tokenPattern = Pattern.compile("<style\\s+token=\"([^\"]+)\"\\s+fg=\"([^\"]+)\"");
				Matcher tokenMatcher = tokenPattern.matcher(xml);
				while (tokenMatcher.find()) {
					result.put(tokenMatcher.group(1), tokenMatcher.group(2));
				}
			}
		} catch (Exception ignored) {
		}
		return result;
	}

	private static String colorToHex(Color c) {
		return String.format("%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
	}

	public void setText(String code) {
		if (code == null) code = "";
		this.cachedCode = code;
		if (isLoaded) {
			String escaped = escapeJSString(code);
			executeAsyncJS("setEditorValue('" + escaped + "');");
		}
	}

	public String getText() {
		return cachedCode;
	}

	public void setLanguage(String languageOrExtension) {
		String monacoLang = mapToMonacoLanguage(languageOrExtension);
		this.currentLanguage = monacoLang;
		if (isLoaded) {
			executeAsyncJS("setLanguage('" + monacoLang + "');");
		}
	}

	public void setReadOnly(boolean readOnly) {
		this.currentReadOnly = readOnly;
		if (isLoaded) {
			executeAsyncJS("setReadOnly(" + readOnly + ");");
		}
	}

	public void setFontSize(int size) {
		if (isLoaded) {
			executeAsyncJS("setFontSize(" + size + ");");
		}
	}

	public void jumpToLine(int lineNumber) {
		if (isLoaded) {
			executeAsyncJS("jumpToLine(" + lineNumber + ");");
		}
	}

	public void setCaretPosition(int offset) {
		if (isLoaded) {
			executeAsyncJS("setCaretPosition(" + offset + ");");
		}
	}

	public void triggerFind() {
		if (isLoaded) {
			executeAsyncJS("triggerFind();");
		}
	}

	public void triggerReplace() {
		if (isLoaded) {
			executeAsyncJS("triggerReplace();");
		}
	}

	public void formatCode() {
		if (isLoaded) {
			executeAsyncJS("formatCode();");
		}
	}

	public void addChangeListener(ChangeListener listener) {
		changeListeners.add(listener);
	}

	public void setSaveRequestListener(Runnable runnable) {
		this.saveRequestListener = runnable;
	}

	@Override
	public void close() {
		webView.close();
	}

	public static String mapToMonacoLanguage(String langOrExtension) {
		if (langOrExtension == null) return "plaintext";
		String ext = langOrExtension.toLowerCase();
		if (ext.contains(".")) {
			ext = ext.substring(ext.lastIndexOf('.') + 1);
		}

		return switch (ext) {
			case "java", "text/x-java" -> "java";
			case "js", "mjs" -> "javascript";
			case "json", "info", "mcmeta" -> "json";
			case "xml" -> "xml";
			case "yml", "yaml" -> "yaml";
			case "md", "markdown" -> "markdown";
			case "gradle", "groovy" -> "groovy";
			case "mcfunction" -> "mcfunction";
			case "properties", "lang", "ini", "conf", "toml", "classtweaker", "cfg" -> "ini";
			case "html" -> "html";
			case "css" -> "css";
			default -> "plaintext";
		};
	}

	private String escapeJSString(String str) {
		return str
				.replace("\\", "\\\\")
				.replace("'", "\\'")
				.replace("\n", "\\n")
				.replace("\r", "\\r")
				.replace("\"", "\\\"");
	}

	private Consumer<String> openDeclarationListener;

	public void setOpenDeclarationListener(Consumer<String> openDeclarationListener) {
		this.openDeclarationListener = openDeclarationListener;
	}

	public void initForReuse(String code, String languageOrExtension, boolean readOnly) {
		executeAsyncJS("resetPanel();");
		this.cachedCode = code != null ? code : "";
		this.currentLanguage = mapToMonacoLanguage(languageOrExtension);
		this.currentReadOnly = readOnly;

		if (isLoaded) {
			applyInitSettings();
		}
	}

	public void resetForPool() {
		this.changeListeners.clear();
		this.saveRequestListener = null;
		this.openDeclarationListener = null;
		this.cachedCode = "";
		this.workspace = null;
	}

	public class MonacoBridge {
		public void onTextChanged(String newCode) {
			cachedCode = newCode;
			ThreadUtil.runOnSwingThread(() -> {
				ChangeEvent event = new ChangeEvent(MonacoEditorPanel.this);
				for (ChangeListener listener : changeListeners) {
					listener.stateChanged(event);
				}
			});
		}

		public void onSaveRequested() {
			if (saveRequestListener != null) {
				ThreadUtil.runOnSwingThread(saveRequestListener);
			}
		}

		public void onOpenDeclaration(String word) {
			if (openDeclarationListener != null) {
				ThreadUtil.runOnSwingThread(() -> openDeclarationListener.accept(word));
			}
		}

		public void getDotCompletions(String targetName, String code, Consumer<Object> callback) {
			new Thread(() -> {
				List<JavaTypeResolver.CompletionItem> items = JavaTypeResolver.getCompletionsFor(targetName, code, workspace);
				if (callback != null) {
					callback.accept(new Object[] { items });
				}
			}, "MonacoDotCompletions").start();
		}

		public void onEditorReady() {
			applyInitSettings();
		}
	}
}