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

import net.mcreator.blockly.BlocklyBlockUtil;
import net.mcreator.blockly.BlocklyCompileNote;
import net.mcreator.blockly.BlocklyToCode;
import net.mcreator.blockly.IBlockGenerator;
import net.mcreator.blockly.data.*;
import net.mcreator.generator.template.TemplateGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.XMLUtil;
import org.w3c.dom.Element;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BlocklyBlockCodeGenerator {

	private final Map<String, ToolboxBlock> blocks;

	@Nullable private final TemplateGenerator templateGenerator;
	@Nullable private final Map<String, Object> additionalData;

	private int customBlockIndex = 0;

	private String templateExtension = "java";

	private final Set<String> supportedBlocksGenerator;

	public BlocklyBlockCodeGenerator(Map<String, ToolboxBlock> blocks, Set<String> supportedBlocksGenerator) {
		this(blocks, supportedBlocksGenerator, null, null);
	}

	public BlocklyBlockCodeGenerator(Map<String, ToolboxBlock> blocks, Set<String> supportedBlocksGenerator,
			@Nullable TemplateGenerator templateGenerator, @Nullable Map<String, Object> additionalData) {
		this.blocks = blocks;
		this.supportedBlocksGenerator = supportedBlocksGenerator;
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

		// check if the block does work inside statement blocks
		if (toolboxBlock.error_in_statement_blocks && !master.getStatementInputsMatching(si -> true).isEmpty()) {
			master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
					L10N.t("blockly.errors.block_errors_in_statements", type)));
			return;
		}

		// add dependencies to the master
		if (toolboxBlock.getDependencies() != null)
			toolboxBlock.getDependencies().forEach(master::addDependency);

		Map<String, Object> dataModel = new HashMap<>();

		dataModel.put("parent", master.getParent());

		// we get the list of all elements present in the actual xml
		List<Element> elements = XMLUtil.getDirectChildren(block);

		// check for all fields if they exist, if they do, add them to data model
		if (toolboxBlock.getFields() != null) {
			for (String fieldName : toolboxBlock.getFields()) {
				boolean found = false;
				for (Element element : elements) {
					if (element.getNodeName().equals("field") && element.getAttribute("name").equals(fieldName)
							&& !element.getTextContent().isEmpty()) {
						found = true;
						dataModel.put("field$" + fieldName, element.getTextContent());
						break; // found, no need to look other elements
					}
				}
				if (!found) {
					master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
							L10N.t("blockly.errors.field_not_defined", fieldName, type)));
				}
			}
		}

		// next we check for inputs if they exist, we process them and add to data model
		if (!toolboxBlock.getInputs().isEmpty()) {
			for (String inputName : toolboxBlock.getInputs()) {
				boolean found = false;
				for (Element element : elements) {
					if (element.getNodeName().equals("value") && element.getAttribute("name").equals(inputName)) {
						found = true;
						String generatedCode = BlocklyToCode.directProcessOutputBlock(master, element);
						dataModel.put("input$" + inputName, generatedCode);
						// We also pass the machine_name (id) of the block that is attached to the input
						dataModel.put("input_id$" + inputName, BlocklyBlockUtil.getInputBlockType(element));
						break; // found, no need to look other elements
					}
				}
				if (!found) {
					master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
							L10N.t("blockly.errors.input_empty", inputName, type)));
				}
			}
		}

		// next we check for advanced inputs if they exist, we process them and add to data model
		if (!toolboxBlock.getAdvancedInputs().isEmpty()) {
			for (AdvancedInput advancedInput : toolboxBlock.getAdvancedInputs()) {
				boolean found = false;
				for (Element element : elements) {
					if (element.getNodeName().equals("value") && element.getAttribute("name")
							.equals(advancedInput.name())) {
						found = true;

						// check if nesting statement block that already provides any dependency with
						// a same name, to avoid compile errors due to variable redefinitions
						if (advancedInput.provides != null) {
							for (Dependency dependency : advancedInput.provides) {
								if (master.checkIfDepProviderInputsProvide(dependency)) {
									master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
											L10N.t("blockly.errors.duplicate_dependencies_provided",
													advancedInput.name())));
									return; // no need to do further processing, this needs to be resolved first by the user
								}
							}
						}

						master.pushDepProviderInputStack(advancedInput);
						String generatedCode = BlocklyToCode.directProcessOutputBlock(master, element);
						master.popDepProviderInputStack();

						dataModel.put("input$" + advancedInput.name(), generatedCode);
						// We also pass the machine_name (id) of the block that is attached to the input
						dataModel.put("input_id$" + advancedInput.name(), BlocklyBlockUtil.getInputBlockType(element));

						break; // found, no need to look other elements
					}
				}
				if (!found) {
					master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
							L10N.t("blockly.errors.input_empty", advancedInput.name(), type)));
				}
			}
		}

		// next we check for statement inputs if they exist, we process them and add to data model
		if (toolboxBlock.getStatements() != null) {
			for (StatementInput statementInput : toolboxBlock.getStatements()) {
				boolean found = false;
				for (Element element : elements) {
					if (element.getNodeName().equals("statement") && element.getAttribute("name")
							.equals(statementInput.name)) {
						found = true;

						// check if nesting statement block that already provides any dependency with
						// a same name, to avoid compile errors due to variable redefinitions
						if (statementInput.provides != null) {
							for (Dependency dependency : statementInput.provides) {
								if (master.checkIfDepProviderInputsProvide(dependency)) {
									master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
											L10N.t("blockly.errors.duplicate_dependencies_provided.statement",
													statementInput.name)));
									return; // no need to do further processing, this needs to be resolved first by the user
								}
							}
						}

						master.pushDepProviderInputStack(statementInput);
						String generatedCode = BlocklyToCode.directProcessStatementBlock(master, element);
						master.popDepProviderInputStack();

						dataModel.put("statement$" + statementInput.name, generatedCode);

						break; // found, no need to look other elements
					}
				}
				if (!found) {
					dataModel.put("statement$" + statementInput.name, "");
					master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
							L10N.t("blockly.warnings.statement_input_empty", statementInput.name, type)));
				}
			}
		}

		// next we check for field groups if they are defined, we process them and add to data model
		if (toolboxBlock.getRepeatingFields() != null) {
			for (RepeatingField fieldEntry : toolboxBlock.getRepeatingFields()) {
				String fieldName = fieldEntry.name();
				Map<String, Element> matchingElements = elements.stream()
						.filter(e -> e.getNodeName().equals("field") && e.getAttribute("name")
								.matches(fieldName + "\\d+"))
						.collect(Collectors.toMap(e -> e.getAttribute("name"), e -> e));
				Element mutation = XMLUtil.getFirstChildrenWithName(block, "mutation");
				Map<Integer, String> processedElements = new HashMap<>();
				for (int i = 0; mutation != null && mutation.hasAttribute("inputs") ?
						i < Integer.parseInt(mutation.getAttribute("inputs")) :
						!matchingElements.isEmpty(); i++) {
					if (matchingElements.containsKey(fieldName + i)) {
						String fieldValue = matchingElements.remove(fieldName + i).getTextContent();
						if (fieldValue != null && !fieldValue.isEmpty()) {
							processedElements.put(i, fieldValue);
							continue;
						}
					}
					processedElements.put(i, null); // we add null at this index to not shift other elements
					master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
							L10N.t("blockly.errors.field_not_defined", fieldName + i, type)));
				}
				if (processedElements.containsValue(null))
					return; // no need to do further processing, this needs to be resolved first by the user

				dataModel.put("field_list$" + fieldName,
						processedElements.entrySet().stream().sorted(Map.Entry.comparingByKey())
								.map(Map.Entry::getValue).toArray(String[]::new));
			}
		}

		// next we check for input groups if they are defined, we process them and add to data model
		if (!toolboxBlock.getRepeatingInputs().isEmpty()) {
			for (String inputName : toolboxBlock.getRepeatingInputs()) {
				Map<String, Element> matchingElements = elements.stream()
						.filter(e -> e.getNodeName().equals("value") && e.getAttribute("name")
								.matches(inputName + "\\d+"))
						.collect(Collectors.toMap(e -> e.getAttribute("name"), e -> e));
				Element mutation = XMLUtil.getFirstChildrenWithName(block, "mutation");
				Map<Integer, String> processedElements = new HashMap<>();
				Map<Integer, String> processedElementTypes = new HashMap<>();
				for (int i = 0; mutation != null && mutation.hasAttribute("inputs") ?
						i < Integer.parseInt(mutation.getAttribute("inputs")) :
						!matchingElements.isEmpty(); i++) {
					if (matchingElements.containsKey(inputName + i)) {
						Element currentInput = matchingElements.remove(inputName + i);
						String generatedCode = BlocklyToCode.directProcessOutputBlock(master, currentInput);
						processedElements.put(i, generatedCode);
						processedElementTypes.put(i, BlocklyBlockUtil.getInputBlockType(currentInput));
					} else {
						// we add null at this index to not shift other elements
						processedElements.put(i, null);
						processedElementTypes.put(i, null);
						master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
								L10N.t("blockly.errors.input_empty", inputName + i, type)));
					}
				}
				if (processedElements.containsValue(null))
					return; // no need to do further processing, this needs to be resolved first by the user

				dataModel.put("input_list$" + inputName,
						processedElements.entrySet().stream().sorted(Map.Entry.comparingByKey())
								.map(Map.Entry::getValue).toArray(String[]::new));
				dataModel.put("input_id_list$" + inputName,
						processedElementTypes.entrySet().stream().sorted(Map.Entry.comparingByKey())
								.map(Map.Entry::getValue).toArray(String[]::new));
			}
		}

		// next we check for advanced input groups if they are defined, we process them and add to data model
		if (!toolboxBlock.getRepeatingAdvancedInputs().isEmpty()) {
			for (AdvancedInput advancedInput : toolboxBlock.getRepeatingAdvancedInputs()) {
				Map<String, Element> matchingElements = elements.stream()
						.filter(e -> e.getNodeName().equals("value") && e.getAttribute("name")
								.matches(advancedInput.name() + "\\d+"))
						.collect(Collectors.toMap(e -> e.getAttribute("name"), e -> e));
				Element mutation = XMLUtil.getFirstChildrenWithName(block, "mutation");
				Map<Integer, String> processedElements = new HashMap<>();
				Map<Integer, String> processedElementTypes = new HashMap<>();
				for (int i = 0; mutation != null && mutation.hasAttribute("inputs") ?
						i < Integer.parseInt(mutation.getAttribute("inputs")) :
						!matchingElements.isEmpty(); i++) {
					if (matchingElements.containsKey(advancedInput.name() + i)) {
						// check if nesting statement block that already provides any dependency with
						// a same name, to avoid compile errors due to variable redefinitions
						if (advancedInput.provides != null) {
							for (Dependency dependency : advancedInput.provides) {
								if (master.checkIfDepProviderInputsProvide(dependency)) {
									master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
											L10N.t("blockly.errors.duplicate_dependencies_provided",
													advancedInput.name() + i)));
									return; // no need to do further processing, this needs to be resolved first by the user
								}
							}
						}

						master.pushDepProviderInputStack(advancedInput);
						Element currentInput = matchingElements.remove(advancedInput.name() + i);
						String generatedCode = BlocklyToCode.directProcessOutputBlock(master, currentInput);
						master.popDepProviderInputStack();

						processedElements.put(i, generatedCode);
						processedElementTypes.put(i, BlocklyBlockUtil.getInputBlockType(currentInput));
					} else {
						// we add null at this index to not shift other elements
						processedElements.put(i, null);
						processedElementTypes.put(i, null);
						master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
								L10N.t("blockly.errors.input_empty", advancedInput.name() + i, type)));
					}
				}
				if (processedElements.containsValue(null))
					return; // no need to do further processing, this needs to be resolved first by the user

				dataModel.put("input_list$" + advancedInput.name(),
						processedElements.entrySet().stream().sorted(Map.Entry.comparingByKey())
								.map(Map.Entry::getValue).toArray(String[]::new));
				dataModel.put("input_id_list$" + advancedInput.name(),
						processedElementTypes.entrySet().stream().sorted(Map.Entry.comparingByKey())
								.map(Map.Entry::getValue).toArray(String[]::new));
			}
		}

		// next we check for statement input groups if they are defined, we process them and add to data model
		if (toolboxBlock.getRepeatingStatements() != null) {
			for (StatementInput statementInput : toolboxBlock.getRepeatingStatements()) {
				Map<String, Element> matchingElements = elements.stream()
						.filter(e -> e.getNodeName().equals("statement") && e.getAttribute("name")
								.matches(statementInput.name() + "\\d+"))
						.collect(Collectors.toMap(e -> e.getAttribute("name"), e -> e));
				Element mutation = XMLUtil.getFirstChildrenWithName(block, "mutation");
				Map<Integer, String> processedElements = new HashMap<>();
				for (int i = 0; mutation != null && mutation.hasAttribute("inputs") ?
						i < Integer.parseInt(mutation.getAttribute("inputs")) :
						!matchingElements.isEmpty(); i++) {
					if (matchingElements.containsKey(statementInput.name + i)) {
						// check if nesting statement block that already provides any dependency with
						// a same name, to avoid compile errors due to variable redefinitions
						if (statementInput.provides != null) {
							for (Dependency dependency : statementInput.provides) {
								if (master.checkIfDepProviderInputsProvide(dependency)) {
									master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
											L10N.t("blockly.errors.duplicate_dependencies_provided.statement",
													statementInput.name + i)));
									return; // no need to do further processing, this needs to be resolved first by the user
								}
							}
						}

						master.pushDepProviderInputStack(statementInput);
						String generatedCode = BlocklyToCode.directProcessStatementBlock(master,
								matchingElements.remove(statementInput.name + i));
						master.popDepProviderInputStack();

						processedElements.put(i, generatedCode);
					} else {
						processedElements.put(i, "");
						master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
								L10N.t("blockly.warnings.statement_input_empty", statementInput.name + i, type)));
					}
				}
				dataModel.put("statement_list$" + statementInput.name,
						processedElements.entrySet().stream().sorted(Map.Entry.comparingByKey())
								.map(Map.Entry::getValue).toArray(String[]::new));
			}
		}

		if (toolboxBlock.getRequiredAPIs() != null) {
			for (String required_api : toolboxBlock.getRequiredAPIs()) {
				if (!master.getWorkspaceSettings().getMCreatorDependencies().contains(required_api)) {
					master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
							L10N.t("blockly.errors.api_required", required_api, type)));
				}
			}
		}

		// add custom warnings if present
		if (toolboxBlock.getWarnings() != null) {
			for (String warning : toolboxBlock.getWarnings()) {
				master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
						L10N.t("blockly.warning." + warning, type)));
			}
		}

		// Some other generator may support this block, so we check if it is supported last in the chain before generating actual code
		// This way dependency structure is not generator dependant and also if there are problems with sub-blocks (inputs, statements, ...),
		// they are reported as those problems may be relevant for some other generator when/if it is switched
		if (toolboxBlock.getType() == IBlockGenerator.BlockType.PROCEDURAL) {
			if (!supportedBlocksGenerator.contains(type)) {
				master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
						L10N.t("blockly.warnings.block_not_supported", type)));
				return;
			}
		} else if (toolboxBlock.getType() == IBlockGenerator.BlockType.OUTPUT) {
			if (!supportedBlocksGenerator.contains(type)) {
				master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
						L10N.t("blockly.errors.block_not_supported", type)));
				return;
			}
		}

		if (templateGenerator != null) {
			dataModel.put("customBlockIndex", customBlockIndex); // kept for backwards compatibility
			dataModel.put("cbi", customBlockIndex);

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
		return blocks_machine_names.computeIfAbsent(blockType,
				key -> blocks.values().stream().filter(block -> block.getType() == key)
						.map(ToolboxBlock::getMachineName).toArray(String[]::new));
	}

}
