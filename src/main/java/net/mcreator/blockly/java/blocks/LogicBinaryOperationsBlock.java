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
import net.mcreator.blockly.java.JavaKeywordsMap;
import net.mcreator.blockly.java.ProcedureCodeOptimizer;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.XMLUtil;
import org.w3c.dom.Element;

import java.util.List;

public class LogicBinaryOperationsBlock implements IBlockGenerator {

	@Override public void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException {
		String blocktype = block.getAttribute("type");
		List<Element> elements = XMLUtil.getDirectChildren(block);

		String operationType = null;
		Element a = null, b = null;
		for (Element element : elements) {
			if (element.getNodeName().equals("field") && element.getAttribute("name").equals("OP"))
				operationType = element.getTextContent();
			else if (element.getNodeName().equals("value"))
				if (element.getAttribute("name").equals("A"))
					a = element;
				else if (element.getAttribute("name").equals("B"))
					b = element;
		}
		if (a != null && b != null) {
			String codeA = BlocklyToCode.directProcessOutputBlock(master, a);
			String codeB = BlocklyToCode.directProcessOutputBlock(master, b);
			if (JavaKeywordsMap.BINARY_LOGIC_OPERATORS.get(operationType) != null) {
				String operator = JavaKeywordsMap.BINARY_LOGIC_OPERATORS.get(operationType);
				master.append("(");
				master.append(withoutParentheses(codeA, blocktype, operator));
				master.append(operator);
				master.append(withoutParentheses(codeB, blocktype, operator));
				master.append(")");
			}
		} else {
			master.append("(true)");
			master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
					L10N.t("blockly.warnings.binary_operations")));
		}
	}

	@Override public String[] getSupportedBlocks() {
		return new String[] { "logic_binary_ops", "math_binary_ops" };
	}

	@Override public BlockType getBlockType() {
		return BlockType.OUTPUT;
	}

	private static String withoutParentheses(String code, String blockType, String operator) {
		String lowerPriority; // Operations that require () because of lower priority or non-associativity
		switch (blockType) {
		case "logic_binary_ops" -> lowerPriority = switch (operator) {
			case "!=", "==" -> "=^&|?"; // = is needed to avoid bad operand types
			case "^" -> "&|?";
			case "&&" -> "|?";
			case "||" -> "?";
			default -> "!=^&|?";
		};
		case "math_binary_ops" -> lowerPriority = "&^|?";
		case null, default -> {
			return code;
		}
		}
		return ProcedureCodeOptimizer.removeParentheses(code, lowerPriority);
	}
}
