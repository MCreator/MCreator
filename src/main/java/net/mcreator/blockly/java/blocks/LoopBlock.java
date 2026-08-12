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

import net.mcreator.blockly.BlocklyBlockUtil;
import net.mcreator.blockly.BlocklyCompileNote;
import net.mcreator.blockly.BlocklyToCode;
import net.mcreator.blockly.IBlockGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.XMLUtil;
import org.w3c.dom.Element;

import javax.annotation.Nullable;

public class LoopBlock implements IBlockGenerator {

	@Override public void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException {
		Element value = XMLUtil.getFirstChildrenWithName(block, "value");
		Element statement = XMLUtil.getFirstChildrenWithName(block, "statement");
		Element mutation = XMLUtil.getFirstChildrenWithName(block, "mutation");

		if (value != null && statement != null) {
			int index = master.getBlockCount();
			if (mutation != null) {
				index = Integer.parseInt(mutation.getAttribute("nesting_level"));
			}

			master.append("for (int _i").append(index).append(" = 0; _i").append(index).append("<");
			master.processOutputBlockToInt(value);
			master.append("; _i").append(index).append("++) {");
			master.processBlockProcedure(BlocklyBlockUtil.getBlockProcedureStartingWithBlock(statement));
			master.append("}");
		} else {
			master.addCompileNote(
					new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING, L10N.t("blockly.warnings.empty_loop")));
		}
	}

	@Override public String[] getSupportedBlocks() {
		return new String[] { "controls_repeat_ext" };
	}

	@Override public BlockType getBlockType() {
		return BlockType.PROCEDURAL;
	}

	@Nullable @Override public String[] getBlockJSONDefinitions() {
		return new String[] { """
        {
            'type': 'controls_repeat_ext',
            'message0': '%{BKY_CONTROLS_REPEAT_TITLE}',
            'args0': [
                {
                    'type': 'input_value',
                    'name': 'TIMES',
                    'check': 'Number',
                    'ariaLabelText': '%{BKY_INPUT_LABEL_LOOP_TIMES}'
                }
            ],
            'message1': '%{BKY_CONTROLS_REPEAT_INPUT_DO} %1',
            'args1': [
                {
                    'type': 'input_statement',
                    'name': 'DO'
                }
            ],
            "mutator": "store_nesting_level",
            'previousStatement': null,
            'nextStatement': null,
            'style': 'loop_blocks',
            'tooltip': '%{BKY_CONTROLS_REPEAT_TOOLTIP}',
            'helpUrl': '%{BKY_CONTROLS_REPEAT_HELPURL}'
        }""" };
	}

	@Nullable @Override public String getToolboxCategory() {
		return "logicloops";
	}
}
