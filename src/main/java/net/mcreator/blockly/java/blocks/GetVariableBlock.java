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
import net.mcreator.blockly.data.StatementInput;
import net.mcreator.blockly.java.BlocklyToProcedure;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.XMLUtil;
import net.mcreator.workspace.elements.VariableElement;
import net.mcreator.workspace.elements.VariableType;
import net.mcreator.workspace.elements.VariableTypeLoader;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GetVariableBlock implements IBlockGenerator {
	private final String[] names;

	public GetVariableBlock() {
		names = VariableTypeLoader.INSTANCE.getAllVariableTypes().stream().map(VariableType::getName)
				.map(s -> "variables_get_" + s).toArray(String[]::new);
	}

	@Override public void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException {
		String type = StringUtils.removeStart(block.getAttribute("type"), "variables_get_");
		VariableType typeObject = VariableTypeLoader.INSTANCE.fromName(type);

		Element variable = XMLUtil.getFirstChildrenWithName(block, "field");
		Element entityInput = XMLUtil.getFirstChildrenWithName(block, "value");
		if (variable != null) {
			String[] varfield = variable.getTextContent().split(":");
			if (varfield.length == 2) {
				String scope = varfield[0];
				String name = varfield[1];

				if (scope.equals("global") && !master.getWorkspace().getVariableElements().stream()
						.map(VariableElement::getName).toList().contains(name)) {
					master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
							L10N.t("blockly.errors.variables.invalid_var", L10N.t("blockly.block.get_var"),
									L10N.t("blockly.errors.remove_block"))));
					return;
				} else if (master instanceof BlocklyToProcedure && scope.equals("local")
						&& !((BlocklyToProcedure) master).getLocalVariables().stream().map(VariableElement::toString)
						.toList().contains(name)) { // check if local variable exists
					master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
							L10N.t("blockly.errors.variables.invalid_local_var", L10N.t("blockly.block.get_var"),
									L10N.t("blockly.errors.remove_block"))));
					return;
				} else if (scope.equals("local") && !(master instanceof BlocklyToProcedure)) {
					master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
							L10N.t("blockly.warnings.variables.local_scope_unsupported")));
					return;
				} else if (scope.equalsIgnoreCase("local")) {
					List<StatementInput> statementInputList = master.getStatementInputsMatching(
							statementInput -> statementInput.disable_local_variables);
					if (!statementInputList.isEmpty()) {
						for (StatementInput statementInput : statementInputList) {
							master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
									L10N.t("blockly.errors.variables.no_local_scope.statement", statementInput.name)));
							break;
						}
						return;
					}
				} else if (scope.equals("global")) {
					scope = master.getWorkspace().getVariableElementByName(name).getScope().name();
					if (scope.equals("GLOBAL_MAP") || scope.equals("GLOBAL_WORLD")) {
						master.addDependency(new Dependency("world", "world"));
					} else if (entityInput == null && (scope.equals("PLAYER_LIFETIME") || scope.equals(
							"PLAYER_PERSISTENT"))) {
						master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
								L10N.t("blockly.errors.variables.missing_entity_input",
										L10N.t("blockly.block.get_var"))));
						return;
					}
				}

				Object getterTemplate = typeObject.getScopeDefinition(master.getWorkspace(),
						scope.toUpperCase(Locale.ENGLISH)).get("get");
				if (getterTemplate == null) {
					master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
							L10N.t("blockly.errors.variables.no_getter_support", type, scope)));
					return;
				}

				String entitycode = null;
				if (entityInput != null)
					entitycode = BlocklyToCode.directProcessOutputBlock(master, entityInput);

				if (master.getTemplateGenerator() != null) {
					Map<String, Object> dataModel = new HashMap<>();
					dataModel.put("name", name);
					dataModel.put("type", type);
					dataModel.put("scope", scope.toUpperCase(Locale.ENGLISH));
					if (entitycode != null)
						dataModel.put("entity", entitycode);

					String code = master.getTemplateGenerator()
							.generateFromString(getterTemplate.toString(), dataModel);
					master.append(code);
				}
			}
		} else {
			master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
					L10N.t("blockly.errors.variables.improperly_defined", L10N.t("blockly.block.get"))));
		}
	}

	@Override public String[] getSupportedBlocks() {
		return names;
	}

	@Override public BlockType getBlockType() {
		return BlockType.OUTPUT;
	}
}
