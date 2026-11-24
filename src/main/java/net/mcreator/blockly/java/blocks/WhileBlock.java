/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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
import org.w3c.dom.NodeList;

public class WhileBlock implements IBlockGenerator {

	@Override public void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException {
		Element value = XMLUtil.getFirstChildrenWithName(block, "value");
		Element statement = XMLUtil.getFirstChildrenWithName(block, "statement");

		if (value != null && statement != null) {
			master.append("while(");
			master.processOutputBlockWithoutParentheses(value);
			master.append(") {");
			master.processBlockProcedure(BlocklyBlockUtil.getBlockProcedureStartingWithBlock(statement));
			master.append("}");

			// while (true) without break can not complete normally, so the code after it is not reachable
			if (isLiteralTrue(value) && !containsFlowControlBlock(statement))
				master.markBlockTerminating();
		} else {
			master.addCompileNote(
					new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING, L10N.t("blockly.warnings.empty_while")));
		}
	}

	private static boolean isLiteralTrue(Element value) {
		Element condition = XMLUtil.getFirstChildrenWithName(value, "block");
		if (condition == null || !"logic_boolean".equals(condition.getAttribute("type")))
			return false;
		Element field = XMLUtil.getFirstChildrenWithName(condition, "field");
		return field != null && "TRUE".equals(field.getTextContent());
	}

	private static boolean containsFlowControlBlock(Element statement) {
		NodeList blocks = statement.getElementsByTagName("block");
		for (int i = 0; i < blocks.getLength(); i++) {
			if ("controls_flow_statements".equals(((Element) blocks.item(i)).getAttribute("type")))
				return true;
		}
		return false;
	}

	@Override public String[] getSupportedBlocks() {
		return new String[] { "controls_while" };
	}

	@Override public BlockType getBlockType() {
		return BlockType.PROCEDURAL;
	}
}
