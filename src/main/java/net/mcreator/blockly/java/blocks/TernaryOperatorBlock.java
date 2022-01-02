/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

import java.util.List;

public class TernaryOperatorBlock implements IBlockGenerator {
	@Override public void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException {
		List<Element> elements = XMLUtil.getDirectChildren(block);

		Element condition = null, thenBlock = null, elseBlock = null;
		boolean useMarkers = false; // Used to properly map blocks and items
		for (Element element : elements) {
			if (element.getAttribute("name").equals("condition"))
				condition = element;
			else if (element.getAttribute("name").equals("THEN"))
				thenBlock = element;
			else if (element.getAttribute("name").equals("ELSE"))
				elseBlock = element;
			else if (element.getAttribute("mark").equals("true"))
				useMarkers = true;
		}
		if (thenBlock != null && elseBlock != null) {
			if ("logic_ternary_op".equals(BlocklyBlockUtil.getInputBlockType(thenBlock)) || "logic_ternary_op".equals(
					BlocklyBlockUtil.getInputBlockType(elseBlock))) {
				master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
						L10N.t("blockly.errors.ternary_operator.nesting")));
			} else {
				if (condition != null) {
					master.append("(");
					master.processOutputBlockWithoutParentheses(condition, "?:");
					master.append(useMarkers ? "/*@?*/" : "?");
					master.processOutputBlockWithoutParentheses(thenBlock, "?:");
					master.append(useMarkers ? "/*@:*/" : ":");
					master.processOutputBlockWithoutParentheses(elseBlock, "?:");
					master.append(")");
				} else {
					master.processOutputBlock(thenBlock);
					master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
							L10N.t("blockly.warnings.ternary_operator.no_condition")));
				}
			}
		} else {
			master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
					L10N.t("blockly.errors.ternary_operator.no_output")));
		}
	}

	@Override public String[] getSupportedBlocks() {
		return new String[] { "logic_ternary_op" };
	}

	@Override public BlockType getBlockType() {
		return BlockType.OUTPUT;
	}
}
