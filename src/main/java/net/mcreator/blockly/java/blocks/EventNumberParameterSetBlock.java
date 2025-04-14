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

import java.util.List;

public class EventNumberParameterSetBlock implements IBlockGenerator {
	@Override public void generateBlock(BlocklyToCode master, Element block) throws TemplateGeneratorException {
		if (master instanceof BlocklyToProcedure procedure) {
			if (procedure.getExternalTrigger() != null) {
				ExternalTrigger trigger = null;

				List<ExternalTrigger> externalTriggers = BlocklyLoader.INSTANCE.getExternalTriggerLoader()
						.getExternalTriggers();
				for (ExternalTrigger externalTrigger : externalTriggers) {
					if (externalTrigger.getID().equals(((BlocklyToProcedure) master).getExternalTrigger())) {
						trigger = externalTrigger;
						break;
					}
				}

				if (trigger == null) {
					master.getCompileNotes().add(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
							L10N.t("blockly.errors.event_number_parameter_set.not_setter")));
					return;
				}

				if (procedure.getParent().getGenerator() != null) {
					//parameters
					GeneratorWrapper generatorWrapper = new GeneratorWrapper(procedure.getParent().getGenerator());

					List<Element> elements = XMLUtil.getDirectChildren(block);
					String value = null, parameter = null;
					//values
					for (Element element : elements) {
						if (element.getNodeName().equals("field")) {
							if (element.getAttribute("name").equals("eventparametersnumber")) {
								parameter = element.getFirstChild().getNodeValue();
							}
						} else if (element.getNodeName().equals("value")) {
							if (element.getAttribute("name").equals("value")) {
								value = BlocklyToCode.directProcessOutputBlock(master, element);
							}
						}
					}
					if (parameter != null) {
						String needEvent = generatorWrapper.map(parameter, "eventparameters", 0);
						if (needEvent == null) {
							return;
						}
						String needTrigger = generatorWrapper.map(parameter, "eventparameters", 2);
						if (!trigger.getID().equals(needTrigger)) {
							master.getCompileNotes().add(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
									L10N.t("blockly.errors.event_number_parameter_set.invalid_trigger", needTrigger,
											trigger.getID())));
							return;
						}
						if (value != null) {
							//if event is null, the instanceof will ignore it.
							master.append("if (event instanceof ").append(needEvent).append(" _event) {");
							master.append("_event.")
									.append(generatorWrapper.map(parameter, "eventparameters", 1)).append("(")
									.append(value).append(");}");
						}
					}
				}
			} else {
				master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
						L10N.t("blockly.errors.event_number_parameter_set.not_setter")));
			}
		} else {
			master.addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
					L10N.t("blockly.errors.unsupported", "blockly.block.event_number_parameter_set")));
		}
	}

	@Override public String[] getSupportedBlocks() {
		return new String[] { "event_number_parameter_set" };
	}

	@Override public BlockType getBlockType() {
		return BlockType.PROCEDURAL;
	}
}
