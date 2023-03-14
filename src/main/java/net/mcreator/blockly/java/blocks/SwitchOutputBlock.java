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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SwitchOutputBlock implements IBlockGenerator {

	@Override public void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException {
		boolean isNumberType = block.getAttribute("type").equals("controls_switch_number_op_get");
		Element value = null, byDefault = null;
		Map<String, Element> cases = new LinkedHashMap<>();
		List<Element> fields = XMLUtil.getChildrenWithName(block, "field");
		for (Element element : XMLUtil.getChildrenWithName(block, "value")) {
			String name = element.getAttribute("name");
			if (name.startsWith("yield")) { // find the corresponding case value for this expression
				Element caseValue = null;
				for (Element candidate : fields) {
					if (candidate.getAttribute("name").equals(name.replace("yield", "case")))
						caseValue = candidate;
				}
				if (caseValue != null) {
					cases.put(caseValue.getTextContent(), element);
				} else {
					master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
							L10N.t("blockly.errors.switch_case_branch_empty")));
				}
			} else {
				switch (name) {
				case "value" -> value = element;
				case "byDefault" -> byDefault = element;
				}
			}
		}

		if (value == null) {
			master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
					L10N.t("blockly.errors.switch_value_missing")));
			return;
		} else if (byDefault == null) {
			master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
					L10N.t("blockly.errors.switch_default_branch_empty")));
			return;
		}

		master.append("(switch (");
		if (isNumberType)
			master.append(ProcedureCodeOptimizer.toInt(master.directProcessOutputBlockWithoutParentheses(value)));
		else
			master.processOutputBlockWithoutParentheses(value);
		master.append(") {");
		for (String caseValue : cases.keySet()) {
			master.append("case " + (isNumberType ? caseValue : "\"" + caseValue + "\"") + " -> ");
			master.processOutputBlock(cases.get(caseValue));
			master.append(";");
		}
		master.append("default -> ");
		master.processOutputBlock(byDefault);
		master.append("; })");
	}

	@Override public String[] getSupportedBlocks() {
		return new String[] { "controls_switch_number_op_get", "controls_switch_string_op_get" };
	}

	@Override public BlockType getBlockType() {
		return BlockType.OUTPUT;
	}
}
