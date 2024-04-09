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
import net.mcreator.blockly.java.BlocklyToJava;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.XMLUtil;
import org.w3c.dom.Element;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class ProcedureCallBlock implements IBlockGenerator {

	@Override public void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException {
		Element procedureField = XMLUtil.getFirstChildrenWithName(block, "field");
		String type = block.getAttribute("type");

		if (procedureField != null && procedureField.getTextContent() != null && !procedureField.getTextContent()
				.isEmpty()) {
			Procedure procedure = new Procedure(procedureField.getTextContent());
			List<Dependency> dependencies = procedure.getDependencies(master.getWorkspace());

			// If the procedure doesn't actually exist, add a warning and skip this block
			if (!procedure.exists) {
				master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
						L10N.t("blockly.warnings.call_procedure.nonexistent", procedure.getName())));
				return;
			}

			List<String> skippedDepsNames = new ArrayList<>(), processedDepsNames = new ArrayList<>();
			List<DependencyInput> depInputs = mapDependencies(master, block, dependencies, skippedDepsNames,
					processedDepsNames);

			// Add a warning for the passed dependencies that aren't used by the selected procedure
			if (!skippedDepsNames.isEmpty()) {
				master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
						L10N.t("blockly.warnings.call_procedure.extra_deps", procedure.getName(),
								String.join(", ", skippedDepsNames))));
			}

			// Handle dependencies in the command editor
			if (master instanceof BlocklyToJava blocklyToJava
					&& blocklyToJava.getEditorType() == BlocklyEditorType.COMMAND_ARG) {
				List<Dependency> dependenciesProvided;
				if (type.equals("old_command")) {
					master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
							L10N.t("blockly.warnings.old_command.note")));
					dependenciesProvided = Arrays.asList(Dependency.fromString(
							"x:number/y:number/z:number/world:world/entity:entity/arguments:cmdcontext/cmdparams:map"));
				} else {
					dependenciesProvided = Arrays.asList(Dependency.fromString(
							"x:number/y:number/z:number/world:world/entity:entity/arguments:cmdcontext"));
				}

				StringBuilder missingdeps = new StringBuilder();
				boolean missingDependencies = false;
				for (Dependency dependency : dependencies) {
					if (!dependenciesProvided.contains(dependency)) {
						missingDependencies = true;
						missingdeps.append(" ").append(dependency.getName());
					}
				}
				if (missingDependencies) {
					master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
							L10N.t("blockly.errors.call_procedure_missing_deps", procedure.getName(),
									missingdeps.toString())));
				}
			}

			if (master.getTemplateGenerator() != null) {
				Map<String, Object> dataModel = new HashMap<>();
				dataModel.put("procedure", procedure.getName());
				dataModel.put("dependencies", dependencies);
				if (type.equals("call_procedure")) {
					dataModel.put("depInputs", depInputs.toArray(DependencyInput[]::new));
				}

				if (type.equals("old_command")) {
					master.append(
							master.getTemplateGenerator().generateFromTemplate("_old_command.java.ftl", dataModel));
				} else {
					master.append(
							master.getTemplateGenerator().generateFromTemplate("_call_procedure.java.ftl", dataModel));
				}
			}
		} else {
			// No procedure is selected, skip this block
			master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
					L10N.t("blockly.warnings.call_procedure.empty")));
		}
	}

	@Nonnull
	static List<DependencyInput> mapDependencies(BlocklyToCode master, Element block, List<Dependency> dependencies,
			List<String> skippedDepsNames, List<String> processedDepsNames) throws TemplateGeneratorException {
		// The procedure dependencies, in a flattened {"name": "type"} map
		Map<String, String> flattenedDeps = dependencies.stream()
				.collect(Collectors.toMap(Dependency::getName, Dependency::getRawType));
		List<DependencyInput> depInputs = new ArrayList<>();

		Element mutation = XMLUtil.getFirstChildrenWithName(block, "mutation");
		if (mutation != null && mutation.hasAttribute("inputs") && !mutation.getAttribute("inputs")
				.equals("undefined")) {
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

		return depInputs;
	}

	@Override public String[] getSupportedBlocks() {
		return new String[] { "call_procedure", "old_command" };
	}

	@Override public BlockType getBlockType() {
		return BlockType.PROCEDURAL;
	}

	/**
	 * The record holds info about a single dependency row in the block (name, type, input code)
	 */
	public record DependencyInput(String name, String type, String value) {}

}