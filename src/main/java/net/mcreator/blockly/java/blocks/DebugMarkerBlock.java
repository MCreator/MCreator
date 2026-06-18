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

package net.mcreator.blockly.java.blocks;

import net.mcreator.blockly.BlocklyCompileNote;
import net.mcreator.blockly.BlocklyToCode;
import net.mcreator.blockly.IBlockGenerator;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.XMLUtil;
import org.w3c.dom.Element;

import javax.annotation.Nullable;

public class DebugMarkerBlock implements IBlockGenerator {

	public static final String CODE_START = "assert Boolean.TRUE; //#dbg:";

	@Override public void generateBlock(BlocklyToCode master, Element block) {
		Element element = XMLUtil.getFirstChildrenWithName(block, "field");
		if (element != null) {
			master.append("\n" + CODE_START + master.getParent().getName() + ":" + element.getTextContent() + "\n");
		} else {
			master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
					L10N.t("blockly.warnings.empty_debug_marker_block")));
		}
	}

	@Override public String[] getSupportedBlocks() {
		return new String[] { "debug_marker" };
	}

	@Override public BlockType getBlockType() {
		return BlockType.PROCEDURAL;
	}

	@Nullable @Override public String[] getBlockJSONDefinitions() {
		return new String[] { """
        {
          "type": "debug_marker",
          "args0": [
              {
                  "type": "field_javaname",
                  "name": "NAME",
                  "text": "marker1"
              }
          ],
          "previousStatement": null,
          "nextStatement": null,
          "colour": "#ef323d"
        }""" };
	}

	@Nullable @Override public String getToolboxCategory() {
		return "advanced";
	}

}
