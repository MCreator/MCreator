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
import net.mcreator.ui.init.L10N;
import net.mcreator.util.XMLUtil;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcedureCallBlock implements IBlockGenerator {

	@Override public void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException {
		Element element = XMLUtil.getFirstChildrenWithName(block, "field");

		if (element != null) {
			Procedure procedure = new Procedure(element.getTextContent());
			procedure.getDependencies(master.getWorkspace()).forEach(master::addDependency);

			if (!procedure.exists) {
				master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
						L10N.t("blockly.warnings.call_procedure.nonexistent", procedure)));
				return;
			}

			Element x = null, y = null, z = null;
			List<Element> values = XMLUtil.getChildrenWithName(block, "value");
			for (Element e : values)
				switch (e.getAttribute("name")) {
				case "x":
					x = e;
					break;
				case "y":
					y = e;
					break;
				case "z":
					z = e;
					break;
				}
			boolean call_at = false;
			if (x != null || y != null || z != null)
				if (x != null && y != null && z != null)
					call_at = true;
				else
					master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
							L10N.t("blockly.warnings.call_procedure.missing_inputs")));

			String xcode = "";
			String ycode = "";
			String zcode = "";

			if (call_at) {
				xcode = BlocklyToCode.directProcessOutputBlock(master, x);
				ycode = BlocklyToCode.directProcessOutputBlock(master, y);
				zcode = BlocklyToCode.directProcessOutputBlock(master, z);
			}

			if (master.getTemplateGenerator() != null) {
				Map<String, Object> dataModel = new HashMap<>();
				dataModel.put("procedure", procedure.getName());
				if (call_at) {
					dataModel.put("x", xcode);
					dataModel.put("y", ycode);
					dataModel.put("z", zcode);
					dataModel.put("dependencies", procedure.getDependencies(master.getWorkspace()));
					String code = master.getTemplateGenerator()
							.generateFromTemplate("_call_procedure_at.java.ftl", dataModel);
					master.append(code);
				} else {
					dataModel.put("dependencies", procedure.getDependencies(master.getWorkspace()));
					String code = master.getTemplateGenerator()
							.generateFromTemplate("_call_procedure.java.ftl", dataModel);
					master.append(code);
				}
			}

		} else {
			master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
					L10N.t("blockly.warnings.call_procedure.empty")));
		}
	}

	@Override public String[] getSupportedBlocks() {
		return new String[] { "call_procedure", "call_procedure_at" };
	}

	@Override public BlockType getBlockType() {
		return BlockType.PROCEDURAL;
	}
}