/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.ui.modgui;

import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.ui.blockly.BlocklyPanel;

import java.util.Set;

public interface IBlocklyPanelHolder {

	Set<BlocklyPanel> getBlocklyPanels();

	default BlocklyPanel getSpecificBlocklyPanel(BlocklyEditorType type) {
		for (BlocklyPanel panel : getBlocklyPanels()) {
			if (panel.getType() == type)
				return panel;
		}
		return null;
	}

	default boolean isInitialXMLValid() {
		return true;
	}

	void addBlocklyChangedListener(BlocklyChangedListener listener);

	interface BlocklyChangedListener {

		void blocklyChanged(BlocklyPanel panel);

	}

}
