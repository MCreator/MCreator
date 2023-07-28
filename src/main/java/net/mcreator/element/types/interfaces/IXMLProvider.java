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

package net.mcreator.element.types.interfaces;

import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.blockly.data.ToolboxBlock;
import net.mcreator.ui.blockly.BlocklyEditorType;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused") public interface IXMLProvider {

	/**
	 * @param type Type of Blockly setup to be acquired.
	 * @return XML data on a mod element instance used by Blockly editors.
	 */
	String getXML(BlocklyEditorType type);

	/**
	 * @param editor Type of Blockly setup to be checked.
	 * @param blocks IDs of blocks to look for.
	 * @return Whether any of specified blocks are used in the defined Blockly setup.
	 */
	default boolean hasSpecificBlocks(String editor, String... blocks) {
		String xml = getXML(BlocklyEditorType.fromName(editor));
		if (xml != null) {
			xml = xml.replaceAll("(deletable|movable|enabled)=\"(true|false)\" ", "");
			for (String block : blocks) {
				if (xml.contains("<block type=\"" + block + "\"") || xml.contains("<shadow type=\"" + block + "\""))
					return true;
			}
		}
		return false;
	}

	/**
	 * @param editor Type of Blockly setup to be checked.
	 * @param categories IDs of categories to look for blocks from.
	 * @return Whether blocks from any of specified categories are used in the defined Blockly setup.
	 */
	default boolean hasCategoryBlocks(String editor, String... categories) {
		BlocklyEditorType bet = BlocklyEditorType.fromName(editor);
		String xml = getXML(bet);
		if (xml != null && bet != null) {
			xml = xml.replaceAll("(deletable|movable|enabled)=\"(true|false)\" ", "");
			List<String> categoryList = Arrays.asList(categories);
			for (ToolboxBlock block : BlocklyLoader.INSTANCE.getBlockLoader(bet).getDefinedBlocks().values()) {
				if (block.getToolboxCategory() != null && categoryList.contains(block.getToolboxCategory().getId())) {
					String type = "type=\"" + block.getMachineName() + "\"";
					if (xml.contains("<block " + type) || xml.contains("<shadow " + type))
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param editor Type of Blockly setup to be checked.
	 * @param groups IDs of groups to look for blocks from.
	 * @return Whether blocks from any of specified groups are used in the defined Blockly setup.
	 */
	default boolean hasGroupBlocks(String editor, String... groups) {
		BlocklyEditorType bet = BlocklyEditorType.fromName(editor);
		String xml = getXML(bet);
		if (xml != null && bet != null) {
			xml = xml.replaceAll("(deletable|movable|enabled)=\"(true|false)\" ", "");
			List<String> groupList = Arrays.asList(groups);
			for (ToolboxBlock block : BlocklyLoader.INSTANCE.getBlockLoader(bet).getDefinedBlocks().values()) {
				if (groupList.contains(block.getGroup())) {
					String type = "type=\"" + block.getMachineName() + "\"";
					if (xml.contains("<block " + type) || xml.contains("<shadow " + type))
						return true;
				}
			}
		}
		return false;
	}

}
