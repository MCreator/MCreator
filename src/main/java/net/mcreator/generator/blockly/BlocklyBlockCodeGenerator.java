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

package net.mcreator.generator.blockly;

import net.mcreator.blockly.BlocklyCompileNote;
import net.mcreator.blockly.BlocklyToCode;
import net.mcreator.blockly.IBlockGenerator;
import net.mcreator.blockly.data.Dependency;
import net.mcreator.blockly.data.StatementInput;
import net.mcreator.blockly.data.ToolboxBlock;
import net.mcreator.generator.template.TemplateGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.util.XMLUtil;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BlocklyBlockCodeGenerator {

	private final Map<String, ToolboxBlock> blocks;

	@Nullable private final TemplateGenerator templateGenerator;
	@Nullable private final Map<String, Object> additionalData;

	private int customBlockIndex = 0;

	private String templateExtension = "java";

	@Nullable private Set<String> supportedBlocksGenerator;

	public BlocklyBlockCodeGenerator(Map<String, ToolboxBlock> blocks, @Nullable Set<String> supportedBlocksGenerator) {
		this(blocks, null, null);
		this.supportedBlocksGenerator = supportedBlocksGenerator;
	}

	public BlocklyBlockCodeGenerator(Map<String, ToolboxBlock> blocks, @Nullable TemplateGenerator templateGenerator,
			@Nullable Map<String, Object> additionalData) {
		this.blocks = blocks;
		this.templateGenerator = templateGenerator;
		this.additionalData = additionalData;
	}

	public BlocklyBlockCodeGenerator setTemplateExtension(String templateExtension) {
		this.templateExtension = templateExtension;
		return this;
	}

	public void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException {
		String type = block.getAttribute("type");

		ToolboxBlock toolboxBlock = blocks.get(type);
		if (toolboxBlock == null)
			return;

		if (supportedBlocksGenerator != null) {
			if (toolboxBlock.type == IBlockGenerator.BlockType.PROCEDURAL) {
				if (!supportedBlocksGenerator.contains(type)) {
					master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
							"Block " + type + " is not supported by the selected generator. It will be skipped."));
					return;
				}
			} else if (toolboxBlock.type == IBlockGenerator.BlockType.OUTPUT) {
				if (!supportedBlocksGenerator.contains(type)) {
					master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
							"Block " + type + " is not supported by the selected generator!"));
					return;
				}
			}
		}

		// check if the block does work inside statement blocks
		if (toolboxBlock.error_in_statement_blocks && !master.getStatementInputsMatching(si -> true).isEmpty()) {
			master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
					"Block " + type + " does not work inside statement blocks!"));
			return;
		}

		// add dependencies to the master
		if (toolboxBlock.dependencies != null)
			toolboxBlock.dependencies.forEach(master::addDependency);

		Map<String, Object> dataModel = new HashMap<>();

		// we get the list of all elements present in the actual xml
		List<Element> elements = XMLUtil.getDirectChildren(block);

		// check for all fields if they exist, if they do, add them to data model
		if (toolboxBlock.fields != null) {
			for (String fieldName : toolboxBlock.fields) {
				boolean found = false;
				for (Element element : elements) {
					if (element.getNodeName().equals("field") && element.getAttribute("name").equals(fieldName)
							&& !element.getTextContent().equals("")) {
						found = true;
						dataModel.put("field$" + fieldName, element.getTextContent());
						break; // found, no need to look other elements
					}
				}
				if (!found) {
					master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
							"Field " + fieldName + " on block " + type + " is not defined."));
				}
			}
		}

		// next we check for inputs if they exist, we process them and add to data model
		if (toolboxBlock.inputs != null) {
			for (String inputName : toolboxBlock.inputs) {
				boolean found = false;
				for (Element element : elements) {
					if (element.getNodeName().equals("value") && element.getAttribute("name").equals(inputName)) {
						found = true;
						String generatedCode = BlocklyToCode.directProcessOutputBlock(master, element);
						dataModel.put("input$" + inputName, generatedCode);
						break; // found, no need to look other elements
					}
				}
				if (!found) {
					master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
							"Input " + inputName + " on block " + type + " is empty."));
				}
			}
		}

		// next we check for statement inputs if they exist, we process them and add to data model
		if (toolboxBlock.statements != null) {
			for (StatementInput statementInput : toolboxBlock.statements) {
				boolean found = false;
				for (Element element : elements) {
					if (element.getNodeName().equals("statement") && element.getAttribute("name")
							.equals(statementInput.name)) {
						found = true;

						// check if nesting statement block that already provides any dependency with
						// a same name, to avoid compile errors due to variable redefinitions
						if (statementInput.provides != null) {
							for (Dependency dependency : statementInput.provides) {
								if (master.checkIfStatementInputsProvide(dependency)) {
									master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
											"Statement input " + statementInput.name
													+ " provides dependencies already provided by parent statement inputs."));
									return; // no need to do further processing, this needs to be resolved first by the user
								}
							}
						}

						master.pushStatementInputStack(statementInput);
						String generatedCode = BlocklyToCode.directProcessStatementBlock(master, element);
						master.popStatementInputStack();

						dataModel.put("statement$" + statementInput.name, generatedCode);

						break; // found, no need to look other elements
					}
				}
				if (!found) {
					dataModel.put("statement$" + statementInput.name, "");
					master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
							"Statement input " + statementInput.name + " on block " + type + " is empty."));
				}
			}
		}

		if (toolboxBlock.required_apis != null) {
			for (String required_api : toolboxBlock.required_apis) {
				if (!master.getWorkspace().getWorkspaceSettings().getMCreatorDependencies().contains(required_api)) {
					master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
							"Block " + type + " requires " + required_api
									+ " enabled in workspace settings, or the current generator does not support it"));
				}
			}
		}

		if (templateGenerator != null) {
			dataModel.put("customBlockIndex", customBlockIndex);

			if (additionalData != null) {
				dataModel.putAll(additionalData);
			}

			String code = templateGenerator.generateFromTemplate(type + "." + templateExtension + ".ftl", dataModel);
			master.append(code);
		}

		customBlockIndex++;
	}

	// supported blocks by type cache for performance
	private final Map<IBlockGenerator.BlockType, String[]> blocks_machine_names = new HashMap<>();

	public String[] getSupportedBlocks(IBlockGenerator.BlockType blockType) {
		if (blocks_machine_names.containsKey(blockType)) {
			return blocks_machine_names.get(blockType);
		} else {
			String[] retval = blocks.values().stream().filter(block -> block.type == blockType)
					.map(block -> block.machine_name).toArray(String[]::new);
			blocks_machine_names.put(blockType, retval);
			return retval;
		}
	}
}
