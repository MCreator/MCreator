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

import com.sun.javafx.webkit.Accessor;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import net.mcreator.blockly.java.BlocklyVariables;
import net.mcreator.io.FileIO;
import net.mcreator.io.OS;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.ThreadUtil;
import net.mcreator.ui.init.L10N;
import net.mcreator.workspace.elements.VariableElement;
import net.mcreator.workspace.elements.VariableElementType;
import netscape.javascript.JSObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class BlocklyPanel extends JFXPanel {

	private static final Logger LOG = LogManager.getLogger("Blockly");

	private WebEngine webEngine;

	private BlocklyJavascriptBridge bridge;

	private final List<Runnable> runAfterLoaded = new ArrayList<>();

	private boolean loaded = false;

	private String currentXML = null;

	public BlocklyPanel(MCreator mcreator) {
		setOpaque(false);
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

					css += FileIO.readResourceToString("/blockly/css/" + UIManager.get("MCreatorLAF.BLOCKLY_CSS"));

					//remove font declaration if property set so
					if (PreferencesManager.PREFERENCES.blockly.legacyFont) {
						css = css.replace("font-family: sans-serif;", "");
					}

					Text styleContent = webEngine.getDocument().createTextNode(css);
					styleNode.appendChild(styleContent);
					webEngine.getDocument().getDocumentElement().getElementsByTagName("head").item(0)
							.appendChild(styleNode);

					// load JS from files here, not in HTML to support Unix systems
					String resDir = "res/";
					if (OS.getOS() != OS.WINDOWS) // path fix for Unix systems
						resDir = "jar:file:./lib/mcreator.jar!/blockly/res/";

					// @formatter:off
					webEngine.executeScript("var MCR_BLCKLY_PREF = { "
							+ "'comments' : " + PreferencesManager.PREFERENCES.blockly.enableComments + ","
							+ "'renderer' : '" + PreferencesManager.PREFERENCES.blockly.blockRenderer.toLowerCase(Locale.ENGLISH) + "',"
							+ "'collapse' : " + PreferencesManager.PREFERENCES.blockly.enableCollapse + ","
							+ "'trashcan' : " + PreferencesManager.PREFERENCES.blockly.enableTrashcan + ","
							+ "'maxScale' : " + PreferencesManager.PREFERENCES.blockly.maxScale/100.0 + ","
							+ "'minScale' : " + PreferencesManager.PREFERENCES.blockly.minScale/100.0 + ","
							+ "'scaleSpeed' : " + PreferencesManager.PREFERENCES.blockly.scaleSpeed/100.0 + ","
							+ " };");
					// @formatter:on

					webEngine.executeScript(FileIO.readResourceToString("/jsdist/msg/messages.js"));
					webEngine.executeScript(FileIO.readResourceToString("/jsdist/msg/" + L10N.getLangString() + ".js"));

					webEngine.executeScript(FileIO.readResourceToString("/blockly/js/block_mcitem.js")
							.replace("@RESOURCES_PATH", resDir));
					webEngine.executeScript(FileIO.readResourceToString("/blockly/js/field_ai_condition.js")
							.replace("@RESOURCES_PATH", resDir));
					webEngine.executeScript(FileIO.readResourceToString("/blockly/js/mcreator_blocks.js")
							.replace("@RESOURCES_PATH", resDir));
					webEngine.executeScript(FileIO.readResourceToString("/blockly/js/mcreator_blockly.js")
							.replace("@RESOURCES_PATH", resDir));

					//JS code generation for custom variables
					for(VariableElementType variable : VariableElement.getVariables()) {
						//We begin by creating the extensions needed for other blocks
						webEngine.executeScript(BlocklyJavascriptTemplates.variableListExtension(variable));
						webEngine.executeScript(BlocklyJavascriptTemplates.procedureListExtensions(variable));
						//Then, we create the blocks related to variables
						webEngine.executeScript(BlocklyJavascriptTemplates.getVariableBlock(variable));
						webEngine.executeScript(BlocklyJavascriptTemplates.setVariableBlock(variable));
						webEngine.executeScript(BlocklyJavascriptTemplates.customDependencyBlock(variable));
						webEngine.executeScript(BlocklyJavascriptTemplates.procedureReturnValueBlock(variable));
						webEngine.executeScript(BlocklyJavascriptTemplates.returnBlock(variable));
					}

					// colorize panel
					Accessor.getPageFor(webEngine).setBackgroundColor(0);

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
		bridge = new BlocklyJavascriptBridge(mcreator, () -> this.currentXML = (String) executeJavaScriptSynchronously(
				"Blockly.Xml.domToText(Blockly.Xml.workspaceToDom(workspace, true))"));
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
		this.currentXML = xml;
	}

	public void addBlocksFromXML(String xml) {
		xml = xml.replace("'", "\\'").replace("\n", "\\n").replace("\r", "\\r"); // escape single quotes and new lines
		executeJavaScriptSynchronously(
				"Blockly.Xml.appendDomToWorkspace(Blockly.Xml.textToDom('" + xml + "'), workspace)");
	}

	public void setXML(String xml) {
		this.currentXML = xml;
		xml = xml.replace("'", "\\'").replace("\n", "\\n").replace("\r", "\\r"); // escape single quotes and new lines
		executeJavaScriptSynchronously("Blockly.Xml.domToWorkspace(Blockly.Xml.textToDom('" + xml + "'), workspace)");
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
		for (String var : vars) {
			String[] vardata = var.split(";");
			if (vardata.length == 2) {
				VariableElement element = new VariableElement();
				element.setName(vardata[0]);
				element.setType(BlocklyVariables.getMCreatorVariableTypeFromBlocklyVariableType(vardata[1]).getType());
				retval.add(element);
			}
		}
		return retval;
	}

	public Object executeJavaScriptSynchronously(String javaScript) {
		try {
			FutureTask<Object> query = new FutureTask<>(() -> webEngine.executeScript(javaScript));
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

}