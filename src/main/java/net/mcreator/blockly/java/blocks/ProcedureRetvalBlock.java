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
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;

public class ProcedureRetvalBlock implements IBlockGenerator {

	@Override public void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException {
		String type;

		String blocktype = block.getAttribute("type");
		switch (blocktype) {
		case "procedure_retval_number":
			type = "NUMBER";
			break;
		case "procedure_retval_string":
			type = "STRING";
			break;
		case "procedure_retval_logic":
			type = "LOGIC";
			break;
		default:
			return;
		}

		Element element = XMLUtil.getFirstChildrenWithName("field", block);

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
		return new String[] { "procedure_retval_logic", "procedure_retval_number", "procedure_retval_string" };
	}

	@Override public BlockType getBlockType() {
		return BlockType.OUTPUT;
	}
}