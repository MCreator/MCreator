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
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.util.XMLUtil;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import java.util.List;

public class BinaryOperationsBlock implements IBlockGenerator {

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
			if (JavaKeywordsMap.BINARY_OPERATORS.get(operationType) != null) {
				String operator = JavaKeywordsMap.BINARY_OPERATORS.get(operationType);
				master.append("(");
				master.append(withoutParentheses(codeA, blocktype, operator));
				master.append(operator);
				master.append(withoutParentheses(codeB, blocktype, operator));
				master.append(")");
			} else if (JavaKeywordsMap.MATH_OPERATORS.get(operationType) != null) {
				master.append("Math.").append(JavaKeywordsMap.MATH_OPERATORS.get(operationType)).append("(");
				master.append(withoutParentheses(codeA));
				master.append(",");
				master.append(withoutParentheses(codeB));
				master.append(")");
			}
		}else {
			master.append(blocktype.equals("logic_binary_ops") ? "(true)" : "0");
			master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
					"One of dual input blocks input is empty. Using default type value for it."));
		}
	}

	@Override public String[] getSupportedBlocks() {
		return new String[] { "logic_binary_ops", "math_binary_ops", "math_dual_ops" };
	}

	@Override public BlockType getBlockType() {
		return BlockType.OUTPUT;
	}

	private static String withoutParentheses(String code, String blockType, String operator) {
		if (canRemoveParentheses(code)) {
			String lowerPriority; // Operations that require () because of lower priority or non-associativity
			if ("logic_binary_ops".equals(blockType)) {
				lowerPriority = switch (operator) {
					case "!=", "==" -> "^&|?";
					case "^" -> "&|?";
					case "&&" -> "|?";
					case "||" -> "?";
					default -> "!=^&|?";
				};
			} else if ("math_dual_ops".equals(blockType)) {
				lowerPriority = switch (operator) {
					case "*" -> "+-/%&^|?";
					case "-" -> "+-&^|?";
					case "+" -> "&^|?";
					case "&" -> "^|?";
					case "^" -> "|?";
					case "|" -> "?";
					default -> "+-*/%&^|?";
				};
			} else if ("math_binary_ops".equals(blockType)) {
				lowerPriority = "&^|?";
			} else {
				return code;
			}
			return StringUtils.containsNone(code, lowerPriority) ? code.substring(1, code.length() - 1) : code;
		}
		return code;
	}

	private static String withoutParentheses(String code) {
		if (canRemoveParentheses(code))
			return code.substring(1, code.length()-1);
		return code;
	}

	private static boolean canRemoveParentheses(String code) {
		if (code.startsWith("(") && code.endsWith(")")) {
			int parentheses = 1;
			for (int i = 1; i < code.length() - 1; i++) {
				if (code.charAt(i) == '(')
					parentheses++;
				else if (code.charAt(i) == ')') {
					if (--parentheses == 0) // The first and last parentheses aren't paired, we can't remove them
						return false;
				}
			}
			return parentheses == 0;
		}
		return false;
	}
}
