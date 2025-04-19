/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

import net.mcreator.blockly.BlocklyBlockUtil;
import net.mcreator.blockly.BlocklyCompileNote;
import net.mcreator.blockly.BlocklyToCode;
import net.mcreator.blockly.IBlockGenerator;
import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.blockly.data.ExternalTrigger;
import net.mcreator.blockly.java.BlocklyToProcedure;
import net.mcreator.generator.GeneratorWrapper;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.XMLUtil;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.List;

public class EventParameterSetBlock implements IBlockGenerator {

	@Override public void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException {
		if (master instanceof BlocklyToProcedure procedure) {
			if (procedure.getExternalTrigger() != null) {
				ExternalTrigger trigger = null;

				List<ExternalTrigger> externalTriggers = BlocklyLoader.INSTANCE.getExternalTriggerLoader()
						.getExternalTriggers();
				for (ExternalTrigger externalTrigger : externalTriggers) {
					if (externalTrigger.getID().equals((procedure.getExternalTrigger()))) {
						trigger = externalTrigger;
						break;
					}
				}

				if (trigger == null) {
					master.getCompileNotes().add(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
							L10N.t("blockly.errors.event_parameter_set.not_setter")));
					return;
				}

				if (procedure.getParent().getGenerator() != null) {
					//generator wrapper
					GeneratorWrapper generatorWrapper = new GeneratorWrapper(procedure.getParent().getGenerator());

					List<Element> elements = XMLUtil.getDirectChildren(block);
					String value = null, parameter = null, valueType = null;
					//values
					for (Element element : elements) {
						if (element.getNodeName().equals("field")) {
							String name = element.getAttribute("name");
							if ("eventparameter".equals(name)) {
								parameter = element.getTextContent();
							} else if ("value".equals(name)) {
								//compatibility with datalist selector,dropdown for later extension
								value = element.getTextContent();
							}
						} else if (element.getNodeName().equals("value")) {
							if (element.getAttribute("name").equals("value")) {
								value = BlocklyToCode.directProcessOutputBlock(master, element);
								valueType = BlocklyBlockUtil.getInputBlockType(element);
							}
						}
					}
					if (parameter != null && value != null && !parameter.isEmpty() && !value.isEmpty()) {
						String needEventClass = generatorWrapper.map(parameter, "eventparameters", 0);
						String needMethod = generatorWrapper.map(parameter, "eventparameters", 1);
						String needTrigger = generatorWrapper.map(parameter, "eventparameters", 2);
						if ("null".equals(needEventClass) || "null".equals(needTrigger) || "null".equals(needMethod)) {
							return;
						}
						if (!trigger.getID().equals(needTrigger)) {
							//if trigger is wrong
							master.getCompileNotes().add(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
									L10N.t("blockly.errors.event_parameter_set.invalid_trigger",
											L10N.t("trigger." + needTrigger), L10N.t("trigger." + trigger.getID()))));
							return;
						}
						if (master.getTemplateGenerator() != null) {
							HashMap<String, Object> datamodel = new HashMap<>();
							datamodel.put("field$parameter", parameter);
							if (valueType != null) {
								datamodel.put("input$value", value);
								datamodel.put("input_id$value", valueType);
							} else {
								datamodel.put("field$value", value);
							}
							//if template need them, they will need generator.map. We may help them here.
							datamodel.put("eventClass", needEventClass);
							datamodel.put("method", needMethod);
							datamodel.put("triggerName", needTrigger);
							master.append("_"+master.getTemplateGenerator()
									.generateFromTemplate(block.getAttribute("type") + ".java.ftl", datamodel));
						}
					}
				}
			} else {
				master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
						L10N.t("blockly.errors.event_parameter_set.not_setter")));
			}
		} else {
			master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
					L10N.t("blockly.errors.unsupported", "blockly.block." + block.getAttribute("type"))));
		}
	}

	@Override public String[] getSupportedBlocks() {
		return new String[] { "event_number_parameter_set", "event_logic_parameter_set" };
	}

	@Override public BlockType getBlockType() {
		return BlockType.PROCEDURAL;
	}
}
