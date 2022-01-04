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
import net.mcreator.blockly.java.ProcedureCodeOptimizer;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.XMLUtil;
import org.w3c.dom.Element;

public class LogicNegateBlock implements IBlockGenerator {

	@Override public void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException {
		Element negated_output_block = XMLUtil.getFirstChildrenWithName(block, "value");
		if (negated_output_block != null) {
			String inputCode = BlocklyToCode.directProcessOutputBlock(master, negated_output_block);
			if (inputCode.equals("(true)"))
				master.append("(false)");
			else if (inputCode.equals("(false)"))
				master.append("(true)");
			else {
				master.append("(!");
				master.append(withoutParentheses(inputCode));
				master.append(")");
			}
		} else {
			master.append("(false)");
			master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
					L10N.t("blockly.warnings.empty_not_block")));
		}
	}

	@Override public String[] getSupportedBlocks() {
		return new String[] { "logic_negate" };
	}

	@Override public BlockType getBlockType() {
		return BlockType.OUTPUT;
	}

	private static String withoutParentheses(String code) {
		if (code.contains("instanceof"))
			return code;
		return ProcedureCodeOptimizer.removeParentheses(code, "=><&|^!?");
	}
}
