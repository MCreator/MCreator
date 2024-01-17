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

import java.util.Map;

/**
 * Event called when Blockly panel is ready to register JS objects.
 * <p>
 * Additional objects can be added by calling {@link #getDOMWindow()} and adding them to the map.
 * MCreator handles threading as objects need to be added on the JavaFX thread, so it is safe to put
 * elements to the DOMWindow from any thread when using this event.
 */
public class BlocklyPanelRegisterJSObjects extends MCREvent {

	private final Map<String, Object> domWindowMembers;
	private final BlocklyPanel blocklyPanel;

	public BlocklyPanelRegisterJSObjects(BlocklyPanel blocklyPanel, Map<String, Object> domWindowMembers) {
		this.blocklyPanel = blocklyPanel;
		this.domWindowMembers = domWindowMembers;
	}

	public Map<String, Object> getDOMWindow() {
		return domWindowMembers;
	}

	public BlocklyPanel getBlocklyPanel() {
		return blocklyPanel;
	}

}
