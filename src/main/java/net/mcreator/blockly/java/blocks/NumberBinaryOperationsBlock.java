/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

public class NumberBinaryOperationsBlock implements IBlockGenerator {
	@Override public void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException {
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

		if (a != null && b != null && operationType != null) {
			String codeA = BlocklyToCode.directProcessOutputBlock(master, a);
			String codeB = BlocklyToCode.directProcessOutputBlock(master, b);
			Type returnType = Type.getWidestType(Type.getType(codeA), Type.getType(codeB));

			if (JavaKeywordsMap.BINARY_MATH_OPERATORS.get(operationType) != null) {
				String operator = JavaKeywordsMap.BINARY_MATH_OPERATORS.get(operationType);
				// Bitwise operators always return an int
				if (operator.equals("&") || operator.equals("^") || operator.equals("|"))
					returnType = Type.INT;

				master.append(returnType.getMarker());
				master.append("(");
				master.append(withoutParentheses(codeA, operator));
				master.append(operator);
				master.append(withoutParentheses(codeB, operator));
				master.append(")");
			} else if (JavaKeywordsMap.MATH_METHODS.get(operationType) != null) {
				master.append("Math.").append(JavaKeywordsMap.MATH_METHODS.get(operationType)).append("(");
				master.append(ProcedureCodeOptimizer.removeParentheses(codeA));
				master.append(",");
				master.append(ProcedureCodeOptimizer.removeParentheses(codeB));
				master.append(")");
			}
		} else {
			master.append("/*@int*/0");
			master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
					L10N.t("blockly.warnings.binary_operations")));
		}

	}

	@Override public String[] getSupportedBlocks() {
		return new String[] { "math_dual_ops" };
	}

	@Override public BlockType getBlockType() {
		return BlockType.OUTPUT;
	}

	private static String withoutParentheses(String code, String operator) {
		// Operations that require () because of lower priority or non-associativity
		String lowerPriority = switch (operator) {
			case "*" -> "+-/%&^|?";
			case "-" -> "+-&^|?";
			case "+" -> "&^|?";
			case "&" -> "^|?";
			case "^" -> "|?";
			case "|" -> "?";
			default -> "+-*/%&^|?";
		};

		// Bitwise operators only accept integer operands
		if (operator.equals("&") || operator.equals("^") || operator.equals("|")) {
			return ProcedureCodeOptimizer.toInt(ProcedureCodeOptimizer.removeParentheses(code, lowerPriority));
		}
		return ProcedureCodeOptimizer.removeParentheses(code, lowerPriority);
	}

	private enum Type {
		INT("/*@int*/"),
		FLOAT("/*@float*/"),
		DOUBLE("");

		private final String marker;

		Type(String marker) {
			this.marker = marker;
		}

		String getMarker() {
			return this.marker;
		}

		static Type getType(String code) {
			if (code.startsWith("/*@int*/"))
				return Type.INT;
			return code.startsWith("/*@float*/") ? Type.FLOAT : Type.DOUBLE;
		}

		static Type getWidestType(Type a, Type b) {
			if (a == Type.DOUBLE || b == Type.DOUBLE)
				return Type.DOUBLE;
			else if (a == Type.FLOAT || b == Type.FLOAT)
				return Type.FLOAT;
			else
				return Type.INT;
		}
	}
}
