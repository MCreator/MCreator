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
import net.mcreator.blockly.data.ExternalBlockLoader;
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

	private final Map<String, ExternalBlockLoader.ToolboxBlock> blocks;

	@Nullable private final TemplateGenerator templateGenerator;
	@Nullable private final Map<String, Object> additionalData;

	private int customBlockIndex = 0;

	private String templateExtension = "java";

	@Nullable private Set<String> supportedBlocksGenerator;

	public BlocklyBlockCodeGenerator(Map<String, ExternalBlockLoader.ToolboxBlock> blocks,
			@Nullable Set<String> supportedBlocksGenerator) {
		this(blocks, null, null);
		this.supportedBlocksGenerator = supportedBlocksGenerator;
	}

	public BlocklyBlockCodeGenerator(Map<String, ExternalBlockLoader.ToolboxBlock> blocks,
			@Nullable TemplateGenerator templateGenerator, @Nullable Map<String, Object> additionalData) {
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

		ExternalBlockLoader.ToolboxBlock toolboxBlock = blocks.get(type);
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

		// add dependencies to the master
		if (toolboxBlock.dependencies != null)
			toolboxBlock.dependencies.forEach(master::addDependency);

		Map<String, Object> dataModel = new HashMap<>();

		// we get the list of all elements present in the actual xml
		List<Element> elements = XMLUtil.getDirectChildren(block);

		// check for all fields if they exist, if they do, add them to data model
		if (toolboxBlock.fields != null) {
			for (ExternalBlockLoader.BlockArgument field : toolboxBlock.fields) {
				boolean found = false;
				for (Element element : elements) {
					if (element.getNodeName().equals("field") && element.getAttribute("name").equals(field.getName())
							&& !element.getTextContent().equals("")) {
						found = true;
						dataModel.put("field$" + field.getName(), element.getTextContent());
					}
				}
				if (!found) {
					master.addCompileNote(new BlocklyCompileNote(
							field.doesNotError() ? BlocklyCompileNote.Type.WARNING : BlocklyCompileNote.Type.ERROR,
							"Field " + field.getName() + " on block " + type + " is not defined."));
				}
			}
		}

		// next we check for inputs if they exist, we process them and add to data model
		if (toolboxBlock.inputs != null) {
			for (ExternalBlockLoader.BlockArgument input : toolboxBlock.inputs) {
				boolean found = false;
				for (Element element : elements) {
					if ((element.getNodeName().equals("value") || element.getNodeName().equals("statement")) && element.getAttribute("name").equals(input.getName())) {
						found = true;
						String generatedCode = BlocklyToCode.directProcessOutputBlock(master, element);
						dataModel.put("input$" + input.getName(), generatedCode);
					}
				}
				if (!found) {
					master.addCompileNote(new BlocklyCompileNote(
							input.doesNotError() ? BlocklyCompileNote.Type.WARNING : BlocklyCompileNote.Type.ERROR,
							"Input " + input.getName() + " on block " + type + " is empty."));
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
