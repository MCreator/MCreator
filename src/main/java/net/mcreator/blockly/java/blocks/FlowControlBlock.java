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

package net.mcreator.blockly.java.blocks;

import net.mcreator.blockly.BlocklyCompileNote;
import net.mcreator.blockly.BlocklyToCode;
import net.mcreator.blockly.IBlockGenerator;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.XMLUtil;
import org.w3c.dom.Element;

import javax.annotation.Nullable;
import java.util.Locale;

public class FlowControlBlock implements IBlockGenerator {

	@Override public void generateBlock(BlocklyToCode master, Element block) {
		Element element = XMLUtil.getFirstChildrenWithName(block, "field");
		if (element != null) {
			master.append(element.getTextContent().toLowerCase(Locale.ENGLISH)).append(";");
		} else {
			master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
					L10N.t("blockly.warnings.flow_control", L10N.t("blockly.warnings.skip"))));
		}
	}

	@Override public String[] getSupportedBlocks() {
		return new String[] { "controls_flow_statements" };
	}

	@Override public BlockType getBlockType() {
		return BlockType.PROCEDURAL;
	}

	@Nullable @Override public String[] getBlockJSONDefinitions() {
		return new String[] { """
        {
          "type": "controls_flow_statements",
          "args0": [
              {
                  "type": "field_dropdown",
                  "name": "FLOW",
                  "options": [
                      ["%{BKY_CONTROLS_FLOW_STATEMENTS_OPERATOR_BREAK}", "BREAK"],
                      ["%{BKY_CONTROLS_FLOW_STATEMENTS_OPERATOR_CONTINUE}", "CONTINUE"]
                  ]
              }
          ],
          "previousStatement": null,
          "style": "loop_blocks",
          "helpUrl": "%{BKY_CONTROLS_FLOW_STATEMENTS_HELPURL}",
          "suppressPrefixSuffix": true,
          "extensions": ["controls_flow_tooltip", "controls_flow_in_loop_check_exclude_wait"]
        }""" };
	}

	@Nullable @Override public String getToolboxCategory() {
		return "logicloops";
	}

}
