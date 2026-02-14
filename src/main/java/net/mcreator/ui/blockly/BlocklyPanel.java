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

import net.mcreator.blockly.data.ExternalTrigger;
import net.mcreator.io.FileIO;
import net.mcreator.plugin.MCREvent;
import net.mcreator.plugin.events.ui.BlocklyPanelRegisterDOMData;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.chromium.CefUtils;
import net.mcreator.ui.chromium.WebView;
import net.mcreator.ui.component.util.ThreadUtil;
import net.mcreator.ui.init.BlocklyJavaScriptsLoader;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.util.ColorUtils;
import net.mcreator.util.TestUtil;
import net.mcreator.workspace.elements.VariableElement;
import net.mcreator.workspace.elements.VariableType;
import net.mcreator.workspace.elements.VariableTypeLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class BlocklyPanel extends JPanel implements Closeable {

	private static final Logger LOG = LogManager.getLogger("Blockly");

	private final WebView webView;

	private final BlocklyJavascriptBridge bridge;

	private final BlockingQueue<Runnable> runAfterLoaded = new LinkedBlockingQueue<>();

	private boolean loaded = false;

	private final MCreator mcreator;
	private final BlocklyEditorType type;

	private final List<ChangeListener> changeListeners = new CopyOnWriteArrayList<>();

	public BlocklyPanel(MCreator mcreator, @Nonnull BlocklyEditorType type) {
		super(new BorderLayout());
		this.mcreator = mcreator;
		this.type = type;

		if (isTransparent()) {
			setBackground(ColorUtils.applyAlpha(Theme.current().getBackgroundColor(), 170));
		}

		bridge = new BlocklyJavascriptBridge(mcreator, () -> ThreadUtil.runOnSwingThread(
				() -> changeListeners.forEach(listener -> listener.stateChanged(new ChangeEvent(BlocklyPanel.this)))));

		webView = new WebView("classloader://blockly/blockly.html", isTransparent());

		add("Center", webView);

		webView.addLoadListener(() -> {
			webView.addStringConstantToDOM("editorType", type.registryName());
			webView.addJavaScriptBridge("javabridge", bridge);

			// Add a transparent class to the body if we support a background image
			if (isTransparent()) {
				webView.executeScript("document.body.classList.add('transparent')", WebView.JSExecutionType.LOCAL_SAFE);
			}

			MCREvent.event(new BlocklyPanelRegisterDOMData(this, webView));

			StringBuilder blocklyJS = new StringBuilder();

			// @formatter:off
			blocklyJS.append("const MCR_BLOCKLY_PREF = {")
					.append("'comments': ").append(PreferencesManager.PREFERENCES.blockly.enableComments.get()).append(",")
					.append("'renderer': '").append(PreferencesManager.PREFERENCES.blockly.blockRenderer.get().toLowerCase(Locale.ENGLISH)).append("',")
					.append("'collapse': ").append(PreferencesManager.PREFERENCES.blockly.enableCollapse.get()).append(",")
					.append("'trashcan': ").append(PreferencesManager.PREFERENCES.blockly.enableTrashcan.get()).append(",")
					.append("'maxTrashContents': ").append(PreferencesManager.PREFERENCES.blockly.maxTrashContents.get()).append(",")
					.append("'maxScale': ").append(PreferencesManager.PREFERENCES.blockly.maxScale.get() / 100.0).append(",")
					.append("'minScale': ").append(PreferencesManager.PREFERENCES.blockly.minScale.get() / 100.0).append(",")
					.append("'startScale': ").append(PreferencesManager.PREFERENCES.blockly.startScale.get() / 100.0).append(",")
					.append("'scaleSpeed': ").append(PreferencesManager.PREFERENCES.blockly.scaleSpeed.get() / 100.0).append(",")
					.append("'saturation':").append(PreferencesManager.PREFERENCES.blockly.colorSaturation.get() / 100.0).append(",")
					.append("'value':").append(PreferencesManager.PREFERENCES.blockly.colorValue.get() / 100.0)
					.append("};");
			// @formatter:on

			// Blockly MCreator definitions
			blocklyJS.append(FileIO.readResourceToString("/blockly/js/mcreator_blockly.js"));

			// Load JavaScript files from plugins
			for (String script : BlocklyJavaScriptsLoader.INSTANCE.getScripts())
				blocklyJS.append(script);

			//JS code generation for custom variables
			blocklyJS.append(VariableTypeLoader.INSTANCE.getVariableBlocklyJS());

			// Execute bundled JavaScript scripts
			webView.executeScript(blocklyJS.toString(), WebView.JSExecutionType.GLOBAL_UNSAFE);

			loaded = true;
			runAfterLoaded.forEach(Runnable::run);
		});
	}

	private boolean isTransparent() {
		return mcreator.hasBackgroundImage() && CefUtils.useOSR();
	}

	public void addTaskToRunAfterLoaded(Runnable runnable) {
		if (!loaded)
			runAfterLoaded.add(runnable);
		else
			runnable.run();
	}

	public void addChangeListener(ChangeListener listener) {
		changeListeners.add(listener);
	}

	private static boolean isValidBlocklyXML(@Nullable String xml) {
		if (xml == null || xml.isBlank())
			return false;
		return xml.trim().startsWith("<xml xmlns=\"https://developers.google.com/blockly/xml\">");
	}

	@Nullable private String lastValidXML = null;

	public synchronized String getXML() {
		// If Blockly JS was loaded, query XML from the workspace using JS
		if (loaded) {
			@Nullable String newXml = webView.executeScript("workspaceToXML()", WebView.JSExecutionType.RETURN_VALUE);

			// XML can become invalid if e.g., JCEF runs out of memory and executeJavaScriptSynchronously times out
			boolean valid = isValidBlocklyXML(newXml);

			if (valid) {
				lastValidXML = newXml;
				return newXml;
			} else if (lastValidXML != null) { // If the XML is not valid, return the last valid XML
				return lastValidXML;
			} else {
				LOG.error("Invalid Blockly XML detected and no last valid XML available");
				TestUtil.failIfTestingEnvironment();
			}
		}
		// In the testing environment, we require XML to be processed through Blockly JS, but
		// in other cases, return initialXML until the Blockly editor is loaded
		else if (!TestUtil.isTestingEnvironment()) {
			return initialXMLBeforeLoad != null ? initialXMLBeforeLoad : "";
		}

		return "";
	}

	@Nullable private String initialXMLBeforeLoad = null;
	private boolean initialXMLQueued = false;

	/**
	 * Sets the initial XML configuration for the Blockly workspace. If the workspace
	 * has not been loaded yet, the provided XML is stored as the initial XML to be
	 * applied upon loading. If the workspace is already loaded, the XML is applied
	 * immediately to the workspace.
	 *
	 * @param xml The XML configuration string to set, representing the Blockly workspace structure.
	 */
	public void setInitialXML(String xml) {
		initialXMLBeforeLoad = xml;

		if (!initialXMLQueued) {
			addTaskToRunAfterLoaded(() -> setXML(initialXMLBeforeLoad));
			initialXMLQueued = true;
		}
	}

	private void setXML(String xml) {
		webView.executeScript("""
				workspace.clear();
				Blockly.Xml.domToWorkspace(Blockly.Xml.textToDom('%s'), workspace);
				workspace.clearUndo();
				""".formatted(escapeXML(xml)), WebView.JSExecutionType.LOCAL_SAFE);

		ThreadUtil.runOnSwingThread(
				() -> changeListeners.forEach(listener -> listener.stateChanged(new ChangeEvent(this))));
	}

	public void addBlocksFromXML(String xml) {
		String cleanXML = escapeXML(cleanupXML(xml));
		int index = cleanXML.indexOf("</block><block"); // Look for separator between two chains of blocks
		if (index == -1) { // The separator wasn't found
			webView.executeScript(
					"Blockly.Xml.appendDomToWorkspace(Blockly.Xml.textToDom('" + cleanXML + "'), workspace)",
					WebView.JSExecutionType.LOCAL_SAFE);
		} else { // We add the blocks separately so that they don't overlap, currently used by feature editor where two chains of blocks are possible
			index += 8; //We add the length of "</block>" to the index
			webView.executeScript(
					"Blockly.Xml.appendDomToWorkspace(Blockly.Xml.textToDom('" + cleanXML.substring(0, index)
							+ "</xml>'), workspace)", WebView.JSExecutionType.LOCAL_SAFE);
			webView.executeScript(
					"Blockly.Xml.appendDomToWorkspace(Blockly.Xml.textToDom('<xml>" + cleanXML.substring(index)
							+ "'), workspace)", WebView.JSExecutionType.LOCAL_SAFE);
		}
	}

	public void addGlobalVariable(String name, String type) {
		webView.executeScript("global_variables.push({name: '" + name + "', type: '" + type + "'})",
				WebView.JSExecutionType.LOCAL_SAFE);
	}

	public void addLocalVariable(String name, String type) {
		webView.executeScript("workspace.createVariable('" + name + "', '" + type + "', '" + name + "')",
				WebView.JSExecutionType.LOCAL_SAFE);
	}

	public void removeLocalVariable(String name) {
		webView.executeScript("workspace.deleteVariableById('" + name + "')", WebView.JSExecutionType.LOCAL_SAFE);
	}

	public List<VariableElement> getLocalVariablesList() {
		String query = webView.executeScript("getSerializedLocalVariables()", WebView.JSExecutionType.RETURN_VALUE);
		List<VariableElement> retval = new ArrayList<>();
		if (query == null)
			return retval;

		String[] vars = query.split(":");
		for (String varNameType : vars) {
			String[] vardata = varNameType.split(";");
			if (vardata.length == 2) {
				VariableType variableType = VariableTypeLoader.INSTANCE.fromName(vardata[1]);
				if (variableType != null) {
					VariableElement element = new VariableElement(vardata[0]);
					element.setType(variableType);
					retval.add(element);
				}
			}
		}
		return retval;
	}

	public void executeLocalScript(String script) {
		webView.executeScript(script, WebView.JSExecutionType.LOCAL_SAFE);
	}

	public MCreator getMCreator() {
		return mcreator;
	}

	public BlocklyEditorType getType() {
		return type;
	}

	private String cleanupXML(String xml) {
		return xml.replace("xmlns=\"http://www.w3.org/1999/xhtml\"", "");
	}

	private String escapeXML(String xml) {
		return xml // escape single quotes, new lines, and escapes
				.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n").replace("\r", "\\r");
	}

	public void addExternalTrigger(ExternalTrigger external_trigger) {
		bridge.addExternalTrigger(external_trigger);
	}

	@Override public void close() {
		webView.close();
	}

}