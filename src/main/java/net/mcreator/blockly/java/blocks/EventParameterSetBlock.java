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
		if (master instanceof BlocklyToProcedure blocklyToProcedure) {
			ExternalTrigger trigger = null;

			List<ExternalTrigger> externalTriggers = BlocklyLoader.INSTANCE.getExternalTriggerLoader()
					.getExternalTriggers();
			for (ExternalTrigger externalTrigger : externalTriggers) {
				if (externalTrigger.getID().equals(blocklyToProcedure.getExternalTrigger())) {
					trigger = externalTrigger;
					break;
				}
			}

			if (trigger == null) {
				master.getCompileNotes().add(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
						L10N.t("blockly.errors.event_parameter_set.no_selected_trigger")));
				return;
			}

			GeneratorWrapper generatorWrapper = new GeneratorWrapper(master.getParent().getGenerator());

			List<Element> elements = XMLUtil.getDirectChildren(block);
			String value = null, parameter = null;
			for (Element element : elements) {
				if ("field".equals(element.getNodeName())) {
					String name = element.getAttribute("name");
					if ("eventparameter".equals(name)) {
						parameter = element.getTextContent();
					} else if ("value".equals(name)) {
						// compatibility with datalist selector, dropdown and so on for later extension
						value = element.getTextContent();
					}
				} else if ("value".equals(element.getNodeName())) {
					if ("value".equals(element.getAttribute("name"))) {
						value = BlocklyToCode.directProcessOutputBlock(master, element);
					}
				}
			}

			if (parameter == null || parameter.isEmpty()) {
				master.getCompileNotes().add(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
						L10N.t("blockly.errors.event_parameter_set.no_selected_parameter")));
				return;
			}

			if (value == null || value.isEmpty()) {
				// skip the block when its value is null; guessing default value would be too complex
				master.getCompileNotes().add(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
						L10N.t("blockly.errors.event_parameter_set.no_value")));
				return;
			}

			String eventClass = generatorWrapper.map(parameter, "eventparameters", 0);
			String eventParameterMethod = generatorWrapper.map(parameter, "eventparameters", 1);
			String requiredGlobalTrigger = generatorWrapper.map(parameter, "eventparameters", 2);

			if (!trigger.getID().equals(requiredGlobalTrigger)) {
				// if requiredGlobalTrigger is null, we also should notify the end-user
				master.getCompileNotes().add(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
						L10N.t("blockly.errors.event_parameter_set.invalid_trigger",
								"null".equals(requiredGlobalTrigger) ?
										"None" :
										L10N.t("trigger." + requiredGlobalTrigger),
								L10N.t("trigger." + trigger.getID()))));
				return;
			}

			if (master.getTemplateGenerator() != null) {
				HashMap<String, Object> datamodel = new HashMap<>();
				datamodel.put("fieldParameterName", parameter);
				datamodel.put("inputValue", value);
				datamodel.put("type", block.getAttribute("type").split("_")[1]); // event_{type}_parameter_set
				// parameters model
				datamodel.put("eventClass", eventClass);
				datamodel.put("method", eventParameterMethod);
				datamodel.put("triggerName", requiredGlobalTrigger);
				master.append(
						master.getTemplateGenerator().generateFromTemplate("_event_parameter_set.java.ftl", datamodel));
			}
		} else {
			master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
					L10N.t("blockly.errors.unsupported", "blockly.block." + block.getAttribute("type"))));
		}
	}

	@Override public String[] getSupportedBlocks() {
		return new String[] { "event_number_parameter_set", "event_logic_parameter_set" }; // event_{type}_parameter_set
	}

	@Override public BlockType getBlockType() {
		return BlockType.PROCEDURAL;
	}

}
