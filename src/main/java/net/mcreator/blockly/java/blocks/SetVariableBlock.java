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
import net.mcreator.util.XMLUtil;
import net.mcreator.workspace.elements.VariableElement;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class SetVariableBlock implements IBlockGenerator {

	@Override public void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException {
		String type;

		String blocktype = block.getAttribute("type");
		switch (blocktype) {
		case "variables_set_number":
			type = "NUMBER";
			break;
		case "variables_set_text":
			type = "STRING";
			break;
		case "variables_set_logic":
			type = "LOGIC";
			break;
		case "variables_set_itemstack":
			type = "ITEMSTACK";
			break;
		default:
			return;
		}

		Element variable = XMLUtil.getFirstChildrenWithName(block, "field");
		Element value = XMLUtil.getFirstChildrenWithName(block, "value");
		if (variable != null && value != null && variable.getTextContent() != null) {
			String[] varfield = variable.getTextContent().split(":");
			if (varfield.length == 2) {
				String scope = varfield[0];
				String name = varfield[1];

				if (scope.equals("global") && !master.getWorkspace().getVariableElements().stream()
						.map(VariableElement::getName).collect(Collectors.toList()).contains(name)) {
					master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
							"Variable set block is bound to a variable that does not exist. Skipping this block."));
					return;
				} else if (master instanceof BlocklyToProcedure && scope.equals("local")
						&& !((BlocklyToProcedure) master).getVariables()
						.contains(name)) { // check if local variable exists
					master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
							"Variable set block is bound to a local variable that does not exist. Skipping this block."));
					return;
				} else if (scope.equals("local") && !(master instanceof BlocklyToProcedure)) {
					master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
							"This editor does not support local variables! Skipping this block"));
					return;
				}
				else if (scope.equalsIgnoreCase("local")) {
					List<StatementInput> statementInputList = master.getLocalVariableDisablingStatements();
					if (statementInputList.size() > 0) {
						for (StatementInput statementInput : statementInputList) {
							AtomicBoolean flag = new AtomicBoolean(false);
							master.getCompileNotes().forEach((note) -> {if (note.getMessage().equalsIgnoreCase("Statement " + statementInput.name + " doesn't support local variables.")) flag.set(true);});
							if (!flag.get())
								master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR, "Statement " + statementInput.name + " doesn't support local variables."));
							return;
						}
					}
				}

				if (scope.equals("global")) {
					scope = master.getWorkspace().getVariableElementByName(name).getScope().name();
					if (scope.equals("GLOBAL_MAP") || scope.equals("GLOBAL_WORLD")) {
						master.addDependency(new Dependency("world", "world"));
					} else if (scope.equals("PLAYER_LIFETIME") || scope.equals("PLAYER_PERSISTENT")) {
						master.addDependency(new Dependency("entity", "entity"));
					}
				}

				if (master.getTemplateGenerator() != null) {
					Map<String, Object> dataModel = new HashMap<>();
					dataModel.put("name", name);
					dataModel.put("scope", scope);
					dataModel.put("type", type);
					dataModel.put("value", BlocklyToCode.directProcessOutputBlock(master, value));

					String code = master.getTemplateGenerator()
							.generateFromTemplate("_set_variable.java.ftl", dataModel);
					master.append(code);
				}
			}
		} else {
			master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
					"Variable set block is not well defined. Skipping it."));
		}
	}

	@Override public String[] getSupportedBlocks() {
		return new String[] { "variables_set_number", "variables_set_text", "variables_set_logic",
				"variables_set_itemstack" };
	}

	@Override public BlockType getBlockType() {
		return BlockType.PROCEDURAL;
	}
}
