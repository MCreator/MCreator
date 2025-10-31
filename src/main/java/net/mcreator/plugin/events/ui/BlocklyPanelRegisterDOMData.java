/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.plugin.events.ui;

import net.mcreator.plugin.MCREvent;
import net.mcreator.ui.blockly.BlocklyPanel;
import net.mcreator.ui.chromium.WebView;

/**
 * Event called when the Blockly panel is ready to register additional DOM data.
 */
public class BlocklyPanelRegisterDOMData extends MCREvent {

	private final BlocklyPanel blocklyPanel;
	private final WebView webView;

	public BlocklyPanelRegisterDOMData(BlocklyPanel blocklyPanel, WebView webView) {
		this.blocklyPanel = blocklyPanel;
		this.webView = webView;
	}

	public void addJavaScriptBridge(String name, Object bridge) {
		webView.addJavaScriptBridge(name, bridge);
	}

	public void addCSSToDOM(String css) {
		webView.addCSSToDOM(css);
	}

	public void addStringConstantToDOM(String name, String value) {
		webView.addStringConstantToDOM(name, value);
	}

	public synchronized void executeScript(String javaScript) {
		webView.executeScript(javaScript);
	}

	public BlocklyPanel getBlocklyPanel() {
		return blocklyPanel;
	}

}
