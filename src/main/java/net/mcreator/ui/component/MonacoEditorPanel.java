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

import com.google.gson.Gson;
import net.mcreator.java.ImportTreeBuilder;
import net.mcreator.java.JavaTypeResolver;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.chromium.WebView;
import net.mcreator.ui.component.util.ThreadUtil;
import net.mcreator.ui.ide.themes.LegacyCodeEditorThemes;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class MonacoEditorPanel extends JPanel implements Closeable {

	private static final Logger LOG = LogManager.getLogger(MonacoEditorPanel.class);

	private final WebView webView;
	private final List<ChangeListener> changeListeners = new CopyOnWriteArrayList<>();
	private EditorEventListener editorEventListener;

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

	public void setWorkspace(Workspace workspace) {
		this.workspace = workspace;
		if (isLoaded && workspace != null) {
			sendExternalClassesToMonaco();
		}
	}

	private void sendExternalClassesToMonaco() {
		if (workspace == null) return;
		CompletableFuture.runAsync(() -> {
			String json = buildExternalClassesJson(workspace);
			executeAsyncJS("setExternalClasses(" + json + ");");
		});
	}

	private record ClassEntry(String name, String pkg) {}

	private static String buildExternalClassesJson(Workspace workspace) {
		if (workspace == null || workspace.getGenerator() == null) return "[]";
		List<ClassEntry> entries = new ArrayList<>();
		Map<String, List<String>> tree = null;
		if (workspace.getGenerator().getGradleCache() != null) {
			tree = workspace.getGenerator().getGradleCache().getImportTree();
		} else if (workspace.getGenerator().getProjectJarManager() != null) {
			tree = ImportTreeBuilder.generateImportTree(workspace.getGenerator().getProjectJarManager());
		}

		if (tree != null) {
			for (Map.Entry<String, List<String>> entry : tree.entrySet()) {
				String className = entry.getKey();
				List<String> fqdns = entry.getValue();
				if (fqdns != null && !fqdns.isEmpty()) {
					String fqdn = fqdns.get(0);
					String pkg = fqdn.contains(".") ? fqdn.substring(0, fqdn.lastIndexOf('.')) : "";
					entries.add(new ClassEntry(className, pkg));
				}
			}
		}

		File srcRoot = workspace.getGenerator().getSourceRoot();
		if (srcRoot != null && srcRoot.isDirectory()) {
			addWorkspaceSourceFiles(srcRoot, srcRoot, entries);
		}

		return new Gson().toJson(entries);
	}

	private static void addWorkspaceSourceFiles(File root, File dir, List<ClassEntry> entries) {
		File[] files = dir.listFiles();
		if (files == null) return;
		for (File f : files) {
			if (f.isDirectory()) {
				addWorkspaceSourceFiles(root, f, entries);
			} else if (f.getName().endsWith(".java")) {
				String name = f.getName().substring(0, f.getName().length() - 5);
				String relPath = root.toPath().relativize(f.toPath()).toString().replace('\\', '.').replace('/', '.');
				String pkg = relPath.contains(".") ? relPath.substring(0, relPath.lastIndexOf('.')) : "";
				if (pkg.endsWith("." + name)) {
					pkg = pkg.substring(0, pkg.length() - name.length() - 1);
				}
				entries.add(new ClassEntry(name, pkg));
			}
		}
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	private void executeAsyncJS(String script) {
		if (webView != null) {
			webView.executeScript(script, WebView.JSExecutionType.LOCAL_SAFE);
		}
	}

	private void applyInitSettings() {
		int fontSize = PreferencesManager.PREFERENCES.ide.fontSize.get();
		boolean autocomplete = PreferencesManager.PREFERENCES.ide.autocomplete.get();
		String autocompleteMode = PreferencesManager.PREFERENCES.ide.autocompleteMode.get();
		boolean autocompleteDocWindow = PreferencesManager.PREFERENCES.ide.autocompleteDocWindow.get();

		String escapedCode = escapeJSString(cachedCode);
		String themeJson = getThemeJsonString();

		String script = "initEditor('" + escapedCode + "', '" + currentLanguage + "', " + currentReadOnly + ", " + fontSize + ", " + autocomplete + ", '" + autocompleteMode + "', " + autocompleteDocWindow + ");";
		executeAsyncJS(script);

		if (themeJson != null) {
			String uniqueThemeId = "dyn-" + System.nanoTime();
			executeAsyncJS("monaco.editor.defineTheme('" + uniqueThemeId + "', " + themeJson + "); monaco.editor.setTheme('" + uniqueThemeId + "');");
		} else {
			executeAsyncJS("monaco.editor.setTheme('vs-dark');");
		}

		isLoaded = true;

		if (workspace != null) {
			sendExternalClassesToMonaco();
		}
	}

	private String getThemeJsonString() {
		String editorThemePref = PreferencesManager.PREFERENCES.ide.editorTheme.get();
		String jsonStr = null;

		if ("MCreator".equalsIgnoreCase(editorThemePref)) {
			String themeId = Theme.current().getID();
			try {
				InputStream is = PluginLoader.INSTANCE.getResourceAsStream(
						"themes/" + themeId + "/styles/code_editor.json");
				if (is == null) {
					is = PluginLoader.INSTANCE.getResourceAsStream(
							"themes/default_dark/styles/code_editor.json");
				}
				if (is != null) {
					jsonStr = new String(is.readAllBytes(), StandardCharsets.UTF_8);
					is.close();
				}
			} catch (Exception e) {
				LOG.warn("Failed to load editor theme", e);
			}
		} else {
			jsonStr = LegacyCodeEditorThemes.getThemeJson(editorThemePref);
		}

		if (jsonStr == null) return null;

		String accentHex = "#" + colorToHex(Theme.current().getInterfaceAccentColor());
		String bgHex = "#" + colorToHex(Theme.current().getBackgroundColor());
		String fgHex = "#" + colorToHex(Theme.current().getForegroundColor());
		String altBgHex = "#" + colorToHex(Theme.current().getAltBackgroundColor());
		String altFgHex = "#" + colorToHex(Theme.current().getAltForegroundColor());
		String secAltBgHex = "#" + colorToHex(Theme.current().getSecondAltBackgroundColor());

		jsonStr = jsonStr.replace("${mainTint}", accentHex)
				.replace("${backgroundColor}", bgHex)
				.replace("${foregroundColor}", fgHex)
				.replace("${altBackgroundColor}", altBgHex)
				.replace("${altForegroundColor}", altFgHex)
				.replace("${secondAltBackgroundColor}", secAltBgHex);

		return jsonStr;
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

	public void showNotification(String message) {
		if (isLoaded && message != null) {
			String escaped = escapeJSString(message);
			executeAsyncJS("showNotification('" + escaped + "');");
		}
	}

	public void addChangeListener(ChangeListener listener) {
		changeListeners.add(listener);
	}

	public void setEditorEventListener(EditorEventListener listener) {
		this.editorEventListener = listener;
	}

	public void setBreakpoints(int[] lines) {
		if (!isLoaded || webView == null) return;
		StringBuilder js = new StringBuilder("if(window.setBreakpoints) window.setBreakpoints([");
		for (int i = 0; i < lines.length; i++) {
			js.append(lines[i]);
			if (i < lines.length - 1) js.append(",");
		}
		js.append("]);");
		executeAsyncJS(js.toString());
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
			case "fsh", "vsh" -> "glsl";
			case "csv" -> "csv";
			default -> "plaintext";
		};
	}

	private String escapeJSString(String str) {
		return str.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n").replace("\r", "\\r")
				.replace("\"", "\\\"");
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
		this.editorEventListener = null;
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

		private void invokeListener(Runnable action) {
			if (editorEventListener != null) {
				ThreadUtil.runOnSwingThread(action);
			}
		}

		public void onSaveRequested() {
			invokeListener(() -> editorEventListener.onSaveRequested());
		}

		public void onSaveAndBuildRequested() {
			invokeListener(() -> editorEventListener.onSaveAndBuildRequested());
		}

		public void onBreakpointToggled(String lineStr) {
			try {
				int line = Integer.parseInt(lineStr);
				invokeListener(() -> editorEventListener.onBreakpointToggled(line));
			} catch (NumberFormatException e) {
				LOG.warn("bad breakpoint line: {}", lineStr);
			}
		}

		public void onOpenDeclaration(String word) {
			invokeListener(() -> editorEventListener.onOpenDeclaration(word));
		}

		public void getDotCompletions(String targetName, String code, String codeBeforeCursor, Consumer<Object> callback) {
			CompletableFuture.runAsync(() -> {
				List<JavaTypeResolver.CompletionItem> items = JavaTypeResolver.getCompletionsFor(targetName, code, codeBeforeCursor, workspace);
				if (callback != null) {
					invokeListener(() -> callback.accept(new Object[] { items }));
				}
			});
		}

		public void onEditorReady() {
			applyInitSettings();
		}
	}

	public interface EditorEventListener {
		default void onSaveRequested() {}
		default void onSaveAndBuildRequested() {}
		default void onBreakpointToggled(int line) {}
		default void onOpenDeclaration(String word) {}
	}
}