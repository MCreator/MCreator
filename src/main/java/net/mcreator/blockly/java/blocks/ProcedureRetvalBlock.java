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
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.XMLUtil;
import net.mcreator.workspace.elements.VariableType;
import net.mcreator.workspace.elements.VariableTypeLoader;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ProcedureRetvalBlock implements IBlockGenerator {
	private final String[] names;

	public ProcedureRetvalBlock() {
		names = VariableTypeLoader.INSTANCE.getAllVariableTypes().stream().map(VariableType::getName)
				.map(s -> "procedure_retval_" + s).toArray(String[]::new);
	}

	@Override public void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException {
		String type = StringUtils.removeStart(block.getAttribute("type"), "procedure_retval_");

		Element element = XMLUtil.getFirstChildrenWithName(block, "field");

		if (element != null) {
			Procedure procedure = new Procedure(element.getTextContent());
			procedure.getDependencies(master.getWorkspace()).forEach(master::addDependency);

			if (!procedure.exists) {
				master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
						L10N.t("blockly.errors.procedure_retval.nonexistent", procedure.getName())));
				return;
			}

			int paramsCount = 0;
			Map<Integer, String> names = new HashMap<>();
			Map<Integer, String> args = new HashMap<>();
			Element mutation = XMLUtil.getFirstChildrenWithName(block, "mutation");
			if (mutation != null) {
				paramsCount = Integer.parseInt(mutation.getAttribute("params"));
				Map<String, Element> fields = XMLUtil.getChildrenWithName(block, "field").stream()
						.filter(e -> e.getAttribute("name").matches("name\\d+"))
						.collect(Collectors.toMap(e -> e.getAttribute("name"), e -> e));
				Map<String, Element> inputs = XMLUtil.getChildrenWithName(block, "value").stream()
						.filter(e -> e.getAttribute("name").matches("arg\\d+"))
						.collect(Collectors.toMap(e -> e.getAttribute("name"), e -> e));
				for (int i = 0; i < paramsCount; i++) {
					names.put(i, fields.get("name" + i).getTextContent());
					if (inputs.containsKey("arg" + i)) {
						args.put(i, BlocklyToCode.directProcessOutputBlock(master, inputs.get("arg" + i)));
					} else {
						args.put(i, "");
						master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
								L10N.t("blockly.errors.call_procedure.missing_inputs")));
					}
				}
			}

			if (master.getTemplateGenerator() != null) {
				Map<String, Object> dataModel = new HashMap<>();
				dataModel.put("procedure", procedure.getName());
				dataModel.put("type", type);
				dataModel.put("dependencies", procedure.getDependencies(master.getWorkspace()));
				dataModel.put("paramsCount", paramsCount);
				dataModel.put("names", names.keySet().stream().sorted().map(names::get).toArray(String[]::new));
				dataModel.put("args", args.keySet().stream().sorted().map(args::get).toArray(String[]::new));

				String code = master.getTemplateGenerator()
						.generateFromTemplate("_procedure_retval.java.ftl", dataModel);
				master.append(code);
			}
		} else {
			master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
					L10N.t("blockly.errors.procedure_retval.empty")));
		}
	}

	@Override public String[] getSupportedBlocks() {
		return names;
	}

	@Override public BlockType getBlockType() {
		return BlockType.OUTPUT;
	}
}