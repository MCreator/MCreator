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

package net.mcreator.ui.blockly;

import javafx.collections.ListChangeListener;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import net.mcreator.io.FileIO;
import net.mcreator.io.OS;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.themes.ThemeLoader;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.ThreadUtil;
import net.mcreator.ui.init.BlocklyJavaScriptsLoader;
import net.mcreator.ui.init.L10N;
import net.mcreator.workspace.elements.VariableElement;
import net.mcreator.workspace.elements.VariableType;
import net.mcreator.workspace.elements.VariableTypeLoader;
import netscape.javascript.JSObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.annotation.Nullable;
import javax.swing.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class BlocklyPanel extends JFXPanel {

	private static final Logger LOG = LogManager.getLogger("Blockly");

	@Nullable private WebEngine webEngine;

	private final BlocklyJavascriptBridge bridge;

	private final List<Runnable> runAfterLoaded = new ArrayList<>();

	private boolean loaded = false;

	private String currentXML = "";

	private final MCreator mcreator;

	private static final String MINIMAL_XML = "<xml xmlns=\"https://developers.google.com/blockly/xml\"></xml>";

	public BlocklyPanel(MCreator mcreator) {
		setOpaque(false);

		this.mcreator = mcreator;

		bridge = new BlocklyJavascriptBridge(mcreator, () -> {
			String newXml = (String) executeJavaScriptSynchronously("workspaceToXML();");

			if (newXml.length() > MINIMAL_XML.length()) {
				this.currentXML = newXml;
				return true;
			}

			return false;
		});

		ThreadUtil.runOnFxThread(() -> {
			WebView browser = new WebView();
			Scene scene = new Scene(browser);
			if (OS.getOS() == OS.WINDOWS) {
				scene.setFill(Color.TRANSPARENT);
			} else {
				java.awt.Color bg = (java.awt.Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT");
				scene.setFill(Color.rgb(bg.getRed(), bg.getGreen(), bg.getBlue()));
			}
			setScene(scene);

			browser.getChildrenUnmodifiable().addListener(
					(ListChangeListener<Node>) change -> browser.lookupAll(".scroll-bar")
							.forEach(bar -> bar.setVisible(false)));
			webEngine = browser.getEngine();
			webEngine.load(BlocklyPanel.this.getClass().getResource("/blockly/blockly.html").toExternalForm());
			webEngine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
				if (!loaded && newState == Worker.State.SUCCEEDED && webEngine.getDocument() != null) {
					// load CSS from file to select proper style for OS
					Element styleNode = webEngine.getDocument().createElement("style");
					String css = FileIO.readResourceToString("/blockly/css/mcreator_blockly.css");

					if (OS.getOS() != OS.WINDOWS) {
						css += FileIO.readResourceToString("/blockly/css/mcreator_blockly_unixfix.css");
					}

					if (PluginLoader.INSTANCE.getResourceAsStream(
							"themes/" + ThemeLoader.CURRENT_THEME.getID() + "/styles/blockly.css") != null) {
						css += FileIO.readResourceToString(PluginLoader.INSTANCE,
								"/themes/" + ThemeLoader.CURRENT_THEME.getID() + "/styles/blockly.css");
					} else {
						css += FileIO.readResourceToString(PluginLoader.INSTANCE,
								"/themes/default_dark/styles/blockly.css");
					}

					//remove font declaration if property set so
					if (PreferencesManager.PREFERENCES.blockly.legacyFont) {
						css = css.replace("font-family: sans-serif;", "");
					}

					Text styleContent = webEngine.getDocument().createTextNode(css);
					styleNode.appendChild(styleContent);
					webEngine.getDocument().getDocumentElement().getElementsByTagName("head").item(0)
							.appendChild(styleNode);

					// @formatter:off
					webEngine.executeScript("var MCR_BLOCKLY_PREF = { "
							+ "'comments' : " + PreferencesManager.PREFERENCES.blockly.enableComments + ","
							+ "'renderer' : '" + PreferencesManager.PREFERENCES.blockly.blockRenderer.toLowerCase(Locale.ENGLISH) + "',"
							+ "'collapse' : " + PreferencesManager.PREFERENCES.blockly.enableCollapse + ","
							+ "'trashcan' : " + PreferencesManager.PREFERENCES.blockly.enableTrashcan + ","
							+ "'maxScale' : " + PreferencesManager.PREFERENCES.blockly.maxScale / 100.0 + ","
							+ "'minScale' : " + PreferencesManager.PREFERENCES.blockly.minScale / 100.0 + ","
							+ "'scaleSpeed' : " + PreferencesManager.PREFERENCES.blockly.scaleSpeed / 100.0 + ","
							+ " };");
					// @formatter:on

					// Blockly core
					webEngine.executeScript(FileIO.readResourceToString("/jsdist/blockly_compressed.js"));
					webEngine.executeScript(
							FileIO.readResourceToString("/jsdist/msg/" + L10N.getBlocklyLangName() + ".js"));
					webEngine.executeScript(FileIO.readResourceToString("/jsdist/blocks_compressed.js"));

					// Blockly MCreator modifications
					webEngine.executeScript(FileIO.readResourceToString("/blockly/js/mcreator_blockly.js"));

					// Load JavaScript files from plugins
					for (String script : BlocklyJavaScriptsLoader.INSTANCE.getScripts())
						webEngine.executeScript(script);

					//JS code generation for custom variables
					webEngine.executeScript(VariableTypeLoader.INSTANCE.getVariableBlocklyJS());

					// Make the webpage transparent
					try {
						Method method = Class.forName("com.sun.javafx.webkit.Accessor")
								.getMethod("getPageFor", WebEngine.class);
						Object accessor = method.invoke(null, webEngine);

						method = Class.forName("com.sun.webkit.WebPage").getMethod("setBackgroundColor", int.class);
						method.invoke(accessor, 0);
					} catch (Exception e) {
						LOG.warn("Failed to set Blockly panel transparency", e);
					}

					// register JS bridge
					JSObject window = (JSObject) webEngine.executeScript("window");
					window.setMember("javabridge", bridge);

					loaded = true;
					runAfterLoaded.forEach(ThreadUtil::runOnFxThread);

					// after blockly is loaded, we resize it to fit the screen
					webEngine.executeScript("Blockly.svgResize(workspace);");
				}
			});
		});
	}

	public void addTaskToRunAfterLoaded(Runnable runnable) {
		if (!loaded)
			runAfterLoaded.add(runnable);
		else
			runnable.run();
	}

	public String getXML() {
		return this.currentXML;
	}

	public void setXMLDataOnly(String xml) {
		this.currentXML = cleanupXML(xml);
	}

	public void addBlocksFromXML(String xml) {
		String cleanXML = escapeXML(cleanupXML(xml));
		int index = cleanXML.indexOf("</block><block"); // Look for separator between two chains of blocks
		if (index == -1) { // The separator wasn't found
			executeJavaScriptSynchronously(
					"Blockly.Xml.appendDomToWorkspace(Blockly.Xml.textToDom('" + cleanXML + "'), workspace)");
		} else { // We add the blocks separately so that they don't overlap, currently used by feature editor where two chains of blocks are possible
			index += 8; //We add the length of "</block>" to the index
			executeJavaScriptSynchronously(
					"Blockly.Xml.appendDomToWorkspace(Blockly.Xml.textToDom('" + cleanXML.substring(0, index)
							+ "</xml>'), workspace)");
			executeJavaScriptSynchronously(
					"Blockly.Xml.appendDomToWorkspace(Blockly.Xml.textToDom('<xml>" + cleanXML.substring(index)
							+ "'), workspace)");
		}
	}

	public void setXML(String xml) {
		this.currentXML = xml;
		executeJavaScriptSynchronously(
				"Blockly.Xml.domToWorkspace(Blockly.Xml.textToDom('" + escapeXML(xml) + "'), workspace)");
		executeJavaScriptSynchronously("workspace.clearUndo()");
	}

	public void clearWorkspace() {
		executeJavaScriptSynchronously("workspace.clear()");
	}

	public void addGlobalVariable(String name, String type) {
		executeJavaScriptSynchronously("global_variables.push({name: '" + name + "', type: '" + type + "'})");
	}

	public void addLocalVariable(String name, String type) {
		executeJavaScriptSynchronously("workspace.createVariable('" + name + "', '" + type + "', '" + name + "')");
	}

	public void removeLocalVariable(String name) {
		executeJavaScriptSynchronously("workspace.deleteVariableById('" + name + "')");
	}

	public List<VariableElement> getLocalVariablesList() {
		String query = (String) executeJavaScriptSynchronously("getSerializedLocalVariables()");
		List<VariableElement> retval = new ArrayList<>();
		if (query == null)
			return retval;

		String[] vars = query.split(":");
		for (String varNameType : vars) {
			String[] vardata = varNameType.split(";");
			if (vardata.length == 2) {
				VariableElement element = new VariableElement();
				element.setName(vardata[0]);
				VariableType variableType = VariableTypeLoader.INSTANCE.fromName(vardata[1]);
				if (variableType != null) {
					element.setType(variableType);
					retval.add(element);
				}
			}
		}
		return retval;
	}

	public Object executeJavaScriptSynchronously(String javaScript) {
		try {
			FutureTask<Object> query = new FutureTask<>(() -> {
				if (webEngine != null)
					return webEngine.executeScript(javaScript);
				return null;
			});
			ThreadUtil.runOnFxThread(query);
			return query.get();
		} catch (InterruptedException | ExecutionException e) {
			LOG.error(javaScript);
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	public BlocklyJavascriptBridge getJSBridge() {
		return bridge;
	}

	public MCreator getMCreator() {
		return mcreator;
	}

	private String cleanupXML(String xml) {
		return xml.replace("xmlns=\"http://www.w3.org/1999/xhtml\"", "");
	}

	private String escapeXML(String xml) {
		return xml // escape single quotes, new lines, and escapes
				.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n").replace("\r", "\\r");
	}

}