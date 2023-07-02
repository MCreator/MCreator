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
import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.XMLUtil;
import net.mcreator.workspace.elements.VariableType;
import net.mcreator.workspace.elements.VariableTypeLoader;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
		Element procedureField = XMLUtil.getFirstChildrenWithName(block, "field");

		if (procedureField != null) {
			Procedure procedure = new Procedure(procedureField.getTextContent());
			List<Dependency> dependencies = procedure.getDependencies(master.getWorkspace());

			// If the procedure doesn't actually exist, add a compile error
			if (!procedure.exists) {
				master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
						L10N.t("blockly.errors.procedure_retval.nonexistent", procedure.getName())));
				return;
			}

			// The procedure dependencies, in a flattened {"name": "type"} map
			Map<String, String> flattenedDeps = dependencies.stream()
					.collect(Collectors.toMap(Dependency::getName, Dependency::getRawType));
			List<DependencyInput> depInputs = new ArrayList<>();
			List<String> skippedDepsNames = new ArrayList<>(), processedDepsNames = new ArrayList<>();

			Element mutation = XMLUtil.getFirstChildrenWithName(block, "mutation");
			if (mutation != null && mutation.hasAttribute("inputs")
					&& !mutation.getAttribute("inputs").equals("undefined")) {
				int depCount = Integer.parseInt(mutation.getAttribute("inputs"));
				Map<String, Element> fields = XMLUtil.getChildrenWithName(block, "field").stream()
						.filter(e -> e.getAttribute("name").matches("name\\d+"))
						.collect(Collectors.toMap(e -> e.getAttribute("name"), e -> e));
				Map<String, Element> inputs = XMLUtil.getChildrenWithName(block, "value").stream()
						.filter(e -> e.getAttribute("name").matches("arg\\d+"))
						.collect(Collectors.toMap(e -> e.getAttribute("name"), e -> e));

				for (int i = 0; i < depCount; i++) {
					String currentName = fields.get("name" + i).getTextContent();
					String currentArg;
					if (inputs.containsKey("arg" + i)) {
						// If the procedure actually has this dependency, also generate the code
						if (flattenedDeps.containsKey(currentName)) {
							currentArg = master.directProcessOutputBlockWithoutParentheses(inputs.get("arg" + i));
						} else {
							// We don't need to do any further processing, skip this dependency
							skippedDepsNames.add(currentName);
							continue;
						}
					} else {
						// Keep processing to look for other missing inputs
						currentArg = "";
						master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
								L10N.t("blockly.errors.call_procedure.missing_inputs", currentName)));
					}
					depInputs.add(new DependencyInput(currentName, flattenedDeps.get(currentName), currentArg));
					processedDepsNames.add(currentName);
				}
			}

			// Add to master all the dependencies that weren't processed
			dependencies.stream().filter(e -> !processedDepsNames.contains(e.getName())).forEach(master::addDependency);

			// Add a warning for the passed dependencies that aren't used by the selected procedure
			if (!skippedDepsNames.isEmpty()) {
				master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
						L10N.t("blockly.warnings.call_procedure.extra_deps", procedure.getName(),
								String.join(", ", skippedDepsNames))));
			}

			if (master.getTemplateGenerator() != null) {
				Map<String, Object> dataModel = new HashMap<>();
				dataModel.put("procedure", procedure.getName());
				dataModel.put("type", type);
				dataModel.put("dependencies", procedure.getDependencies(master.getWorkspace()));
				dataModel.put("depCount", depInputs.size());
				dataModel.put("names", depInputs.stream().map(DependencyInput::name).toArray(String[]::new));
				dataModel.put("types", depInputs.stream().map(DependencyInput::type).toArray(String[]::new));
				dataModel.put("args", depInputs.stream().map(DependencyInput::arg).toArray(String[]::new));

				String code = master.getTemplateGenerator()
						.generateFromTemplate("_procedure_retval.java.ftl", dataModel);
				master.append(code);
			}
		} else {
			// No procedure is selected, add a compile error
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

	// The record holds info about a single dependency row in the block (name, type, input code)
	private record DependencyInput(String name, String type, String arg) {}
}