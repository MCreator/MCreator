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

import java.util.*;
import java.util.stream.Collectors;

public class ProcedureCallBlock implements IBlockGenerator {

	@Override public void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException {
		Element element = XMLUtil.getFirstChildrenWithName(block, "field");
		String type = block.getAttribute("type");

		if (element != null && !"".equals(element.getTextContent())) {
			Procedure procedure = new Procedure(element.getTextContent());
			procedure.getDependencies(master.getWorkspace()).forEach(master::addDependency);

			if (!procedure.exists) {
				master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
						L10N.t("blockly.warnings.call_procedure.nonexistent", procedure.getName())));
				return;
			}

			List<Dependency> dependencies = procedure.getDependencies(master.getWorkspace());
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
					names.put(i, fields.remove("name" + i).getTextContent());
					if (inputs.containsKey("arg" + i)) {
						args.put(i, BlocklyToCode.directProcessOutputBlock(master, inputs.remove("arg" + i)));
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
				dataModel.put("dependencies", dependencies);
				if (type.equals("call_procedure")) {
					dataModel.put("paramsCount", paramsCount);
					dataModel.put("names", names.keySet().stream().sorted().map(names::get).toArray(String[]::new));
					dataModel.put("args", args.keySet().stream().sorted().map(args::get).toArray(String[]::new));
				}

				if (master instanceof BlocklyToJava btj && btj.getEditorType() == BlocklyEditorType.COMMAND_ARG
						&& type.equals("old_command")) {
					master.append(
							master.getTemplateGenerator().generateFromTemplate("_old_command.java.ftl", dataModel));
				} else {
					master.append(
							master.getTemplateGenerator().generateFromTemplate("_call_procedure.java.ftl", dataModel));
				}
			}
		} else {
			master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
					L10N.t("blockly.warnings.call_procedure.empty")));
		}
	}

	@Override public String[] getSupportedBlocks() {
		return new String[] { "call_procedure", "call_procedure_no_args", "old_command" };
	}

	@Override public BlockType getBlockType() {
		return BlockType.PROCEDURAL;
	}
}