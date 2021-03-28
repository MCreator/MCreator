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
import net.mcreator.element.parts.Procedure;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.util.XMLUtil;
import net.mcreator.workspace.elements.VariableElement;
import net.mcreator.workspace.elements.VariableElementType;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProcedureRetvalBlock implements IBlockGenerator {

	@Override public void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException {
		String type = StringUtils.removeStart(block.getAttribute("type"), "procedure_retval_").toUpperCase();

		Element element = XMLUtil.getFirstChildrenWithName(block, "field");

		if (element != null) {
			Procedure procedure = new Procedure(element.getTextContent());
			procedure.getDependencies(master.getWorkspace()).forEach(master::addDependency);

			if (!procedure.exists) {
				master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
						"Procedure return value block is calling nonexistent procedure " + procedure));
				return;
			}

			if (master.getTemplateGenerator() != null) {
				Map<String, Object> dataModel = new HashMap<>();
				dataModel.put("procedure", procedure.getName());
				dataModel.put("type", type);
				dataModel.put("dependencies", procedure.getDependencies(master.getWorkspace()));
				String code = master.getTemplateGenerator()
						.generateFromTemplate("_procedure_retval.java.ftl", dataModel);
				master.append(code);
			}

		} else {
			master.addCompileNote(
					new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR, "Empty procedure return value block"));
		}
	}

	@Override public String[] getSupportedBlocks() {
		Set<String> names = new HashSet<>();
		for (VariableElementType var : VariableElement.getVariables()) {
			names.add("procedure_retval_" + var.getBlockName());
		}
		return names.toArray(new String[0]);
	}

	@Override public BlockType getBlockType() {
		return BlockType.OUTPUT;
	}
}