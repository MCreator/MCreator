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
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.blockly.cef.CEFUtils;
import net.mcreator.ui.component.util.ThreadUtil;
import net.mcreator.ui.init.BlocklyJavaScriptsLoader;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.TestUtil;
import net.mcreator.workspace.elements.VariableElement;
import net.mcreator.workspace.elements.VariableTypeLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandler;
import org.cef.handler.CefLoadHandlerAdapter;

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

	private static final Logger LOG = LogManager.getLogger(BlocklyPanel.class);

	private final CefBrowser cefBrowser;
	private final CefLoadHandler cefLoadHandler;

	private final BlocklyJavascriptBridge bridge;

	private final BlockingQueue<Runnable> runAfterLoaded = new LinkedBlockingQueue<>();

	private boolean loaded = false;

	private final MCreator mcreator;
	private final BlocklyEditorType type;

	private final List<ChangeListener> changeListeners = new CopyOnWriteArrayList<>();

	public BlocklyPanel(MCreator mcreator, @Nonnull BlocklyEditorType type) {
		super(new BorderLayout());
		setOpaque(true);

		this.mcreator = mcreator;
		this.type = type;

		// TODO: handle PreferencesManager.PREFERENCES.blockly.transparentBackground.get() or always transparent if this works?

		cefBrowser = CEFUtils.getCEFClient()
				.createBrowser("data:text/html, " + FileIO.readResourceToString("/blockly/blockly.html"), false, true);

		bridge = new BlocklyJavascriptBridge(mcreator, () -> ThreadUtil.runOnSwingThread(
				() -> changeListeners.forEach(listener -> listener.stateChanged(new ChangeEvent(BlocklyPanel.this)))),
				cefBrowser);

		Component component = cefBrowser.getUIComponent();
		add("Center", component);

		CEFUtils.getMultiLoadHandler().addHandler(cefLoadHandler = new CefLoadHandlerAdapter() {
			@Override public void onLoadEnd(CefBrowser cefBrowserEvent, CefFrame cefFrame, int i) {
				if (cefBrowserEvent == BlocklyPanel.this.cefBrowser) {
					// @formatter:off
					cefBrowser.executeJavaScript("var MCR_BLOCKLY_PREF = { "
							+ "'comments' : " + PreferencesManager.PREFERENCES.blockly.enableComments.get() + ","
							+ "'renderer' : '" + PreferencesManager.PREFERENCES.blockly.blockRenderer.get().toLowerCase(Locale.ENGLISH) + "',"
							+ "'collapse' : " + PreferencesManager.PREFERENCES.blockly.enableCollapse.get() + ","
							+ "'trashcan' : " + PreferencesManager.PREFERENCES.blockly.enableTrashcan.get() + ","
							+ "'maxScale' : " + PreferencesManager.PREFERENCES.blockly.maxScale.get() / 100.0 + ","
							+ "'minScale' : " + PreferencesManager.PREFERENCES.blockly.minScale.get() / 100.0 + ","
							+ "'scaleSpeed' : " + PreferencesManager.PREFERENCES.blockly.scaleSpeed.get() / 100.0 + ","
							+ "'saturation' :" + PreferencesManager.PREFERENCES.blockly.colorSaturation.get() / 100.0 + ","
							+ "'value' :" + PreferencesManager.PREFERENCES.blockly.colorValue.get() / 100.0
							+ " };", "", 0);
					// @formatter:on

					// Blockly core
					cefBrowser.executeJavaScript(FileIO.readResourceToString("/jsdist/blockly_compressed.js"), "", 0);
					cefBrowser.executeJavaScript(
							FileIO.readResourceToString("/jsdist/msg/" + L10N.getBlocklyLangName() + ".js"), "", 0);
					cefBrowser.executeJavaScript(FileIO.readResourceToString("/jsdist/blocks_compressed.js"), "", 0);

					// Blockly MCreator definitions
					cefBrowser.executeJavaScript(FileIO.readResourceToString("/blockly/js/mcreator_blockly.js"), "", 0);

					// Load JavaScript files from plugins
					for (String script : BlocklyJavaScriptsLoader.INSTANCE.getScripts())
						cefBrowser.executeJavaScript(script, "", 0);

					//JS code generation for custom variables
					cefBrowser.executeJavaScript(VariableTypeLoader.INSTANCE.getVariableBlocklyJS(), "", 0);

					loaded = true;
					runAfterLoaded.forEach(Runnable::run);
				}
			}

		});
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
		if (loaded) {
			// TODO: we need to go back to async XML handling
			String newXml = "";
			//@Nullable String newXml = (String) executeJavaScriptSynchronously("workspaceToXML();");

			// XML can become invalid if e.g., WebKit runs out of memory and executeJavaScriptSynchronously times out
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

		return "";
	}

	public void setXML(String xml) {
		executeJavaScriptAsync("""
				workspace.clear();
				Blockly.Xml.domToWorkspace(Blockly.Xml.textToDom('%s'), workspace);
				workspace.clearUndo();
				""".formatted(escapeXML(xml)));

		ThreadUtil.runOnSwingThread(
				() -> changeListeners.forEach(listener -> listener.stateChanged(new ChangeEvent(xml))));
	}

	public void addBlocksFromXML(String xml) {
		String cleanXML = escapeXML(cleanupXML(xml));
		int index = cleanXML.indexOf("</block><block"); // Look for separator between two chains of blocks
		if (index == -1) { // The separator wasn't found
			executeJavaScriptAsync(
					"Blockly.Xml.appendDomToWorkspace(Blockly.Xml.textToDom('" + cleanXML + "'), workspace)");
		} else { // We add the blocks separately so that they don't overlap, currently used by feature editor where two chains of blocks are possible
			index += 8; //We add the length of "</block>" to the index
			executeJavaScriptAsync(
					"Blockly.Xml.appendDomToWorkspace(Blockly.Xml.textToDom('" + cleanXML.substring(0, index)
							+ "</xml>'), workspace)");
			executeJavaScriptAsync(
					"Blockly.Xml.appendDomToWorkspace(Blockly.Xml.textToDom('<xml>" + cleanXML.substring(index)
							+ "'), workspace)");
		}
	}

	public void addGlobalVariable(String name, String type) {
		executeJavaScriptAsync("global_variables.push({name: '" + name + "', type: '" + type + "'})");
	}

	public void addLocalVariable(String name, String type) {
		executeJavaScriptAsync("workspace.createVariable('" + name + "', '" + type + "', '" + name + "')");
	}

	public void removeLocalVariable(String name) {
		executeJavaScriptAsync("workspace.deleteVariableById('" + name + "')");
	}

	public List<VariableElement> getLocalVariablesList() {
		List<VariableElement> retval = new ArrayList<>();

		// TODO: handle this
		/*String query = (String) executeJavaScriptSynchronously("getSerializedLocalVariables()");
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
		}*/

		return retval;
	}

	public void executeJavaScriptAsync(String javaScript) {
		if (cefBrowser != null) {
			cefBrowser.executeJavaScript(javaScript, "", 0);
		}
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

	public void addExternalTriggerForProcedureEditor(ExternalTrigger external_trigger) {
		if (type != BlocklyEditorType.PROCEDURE)
			throw new RuntimeException("This method can only be called from procedure editor");
		bridge.addExternalTrigger(external_trigger);
	}

	@Override public void close() {
		if (cefBrowser != null) {
			CEFUtils.getMultiLoadHandler().removeHandler(cefLoadHandler);
			cefBrowser.getClient().doClose(cefBrowser);
			cefBrowser.close(true);
		}
	}

}