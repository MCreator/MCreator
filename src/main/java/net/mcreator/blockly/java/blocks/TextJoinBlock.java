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

import java.util.ArrayList;
import java.util.List;

public class TextJoinBlock implements IBlockGenerator {

	@Override public void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException {
		Element mutation = XMLUtil.getFirstChildrenWithName(block, "mutation");
		if (mutation != null) {
			int sumnum = Integer.parseInt(mutation.getAttribute("items"));
			List<Element> elements = XMLUtil.getChildrenWithName(block, "value");

			if (sumnum == 0) {
				master.addCompileNote(
						new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR, L10N.t("blockly.errors.join.empty")));
				return;
			}

			List<String> inputCodes = new ArrayList<>();
			for (int i = 0; i < sumnum; i++) {
				boolean match = false;
				for (Element element : elements) {
					if (element.getAttribute("name").equals("ADD" + i)) {
						match = true;
						String code = BlocklyToCode.directProcessOutputBlock(master, element);
						// Remove parentheses, unless it contains operations with same or lower precedence
						inputCodes.add(code.contains("instanceof") ?
								code :
								ProcedureCodeOptimizer.removeParentheses(code, "+-><=&|^?"));
						break;
					}
				}
				if (!match) {
					master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
							L10N.t("blockly.errors.join.undefined_inputs")));
					return;
				}
			}

			if (sumnum == 1) {
				if (inputCodes.get(0).matches("\"[^\"]*\"")) { // The only element is a string, we return it as is
					master.append(inputCodes.get(0));
				} else {
					master.append("(\"\" + " + inputCodes.get(0) + ")");
				}
				return;
			} else {
				master.append("(");
			}

			// If the first two elements aren't strings, we need to add a "" in between
			boolean needsString = !(inputCodes.get(0).matches("\"[^\"]*\"") || inputCodes.get(1).matches("\"[^\"]*\""));
			for (int i = 0; i < sumnum; i++) {
				master.append(inputCodes.get(i));
				if (i == 0 && needsString)
					master.append("+\"\"");
				if (i < sumnum - 1)
					master.append("+");
			}

			master.append(")");
		} else {
			master.addCompileNote(
					new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING, L10N.t("blockly.errors.join.invalid")));
		}
	}

	@Override public String[] getSupportedBlocks() {
		return new String[] { "text_join" };
	}

	@Override public BlockType getBlockType() {
		return BlockType.OUTPUT;
	}
}
