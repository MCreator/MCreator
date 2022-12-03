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
import net.mcreator.ui.init.L10N;
import net.mcreator.util.XMLUtil;
import org.w3c.dom.Element;

import java.util.List;

public class SingularMathOperationsBlock implements IBlockGenerator {

	@Override public void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException {
		List<Element> elements = XMLUtil.getDirectChildren(block);
		String operationType = null;
		Element num = null;
		for (Element element : elements) {
			if (element.getNodeName().equals("field") && element.getAttribute("name").equals("OP"))
				operationType = element.getTextContent();
			else if (element.getNodeName().equals("value") && element.getAttribute("name").equals("NUM"))
				num = element;
		}
		if (operationType != null && JavaKeywordsMap.MATH_OPERATORS.get(operationType) != null && num != null) {
			String numCode = master.directProcessOutputBlockWithoutParentheses(num);
			master.append(switch (operationType) { // We add the proper marker for these operations
				case "ABS" -> {
					if (numCode.startsWith("/*@int*/"))
						yield "/*@int*/";
					else if (numCode.startsWith("/*@float*/"))
						yield "/*@float*/";
					yield "";
				}
				case "ROUND" ->
						numCode.startsWith("/*@int*/") || numCode.startsWith("/*@float*/") ? "/*@int*/" : "/*@float*/";
				case "SIGNUM" -> numCode.startsWith("/*@int*/") || numCode.startsWith("/*@float*/") ? "/*@float*/" : "";
				default -> "";
			});
			master.append("Math.").append(JavaKeywordsMap.MATH_OPERATORS.get(operationType)).append("(");
			master.append(numCode).append(")");
		} else {
			master.append("/*@int*/0");
			master.addCompileNote(
					new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING, L10N.t("blockly.warnings.singular_math")));
		}
	}

	@Override public String[] getSupportedBlocks() {
		return new String[] { "math_singular_ops" };
	}

	@Override public BlockType getBlockType() {
		return BlockType.OUTPUT;
	}
}
