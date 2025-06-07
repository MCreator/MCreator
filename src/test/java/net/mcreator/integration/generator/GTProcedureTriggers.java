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

package net.mcreator.integration.generator;

import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.blockly.data.ExternalTrigger;
import net.mcreator.element.ModElementType;
import net.mcreator.element.types.Procedure;
import net.mcreator.generator.GeneratorWrapper;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.DataListLoader;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.fail;

public class GTProcedureTriggers {

	public static void runTest(Logger LOG, String generatorName, Workspace workspace) {
		Set<String> generatorTriggers = workspace.getGeneratorStats().getProcedureTriggers();

		for (ExternalTrigger externalTrigger : BlocklyLoader.INSTANCE.getExternalTriggerLoader()
				.getExternalTriggers()) {
			if (!generatorTriggers.contains(externalTrigger.getID())) {
				continue;
			}

			if (externalTrigger.required_apis != null) {
				boolean skip = false;

				for (String required_api : externalTrigger.required_apis) {
					if (!workspace.getWorkspaceSettings().getMCreatorDependencies().contains(required_api)) {
						skip = true;
						break;
					}
				}

				if (skip)
					continue;
			}

			ModElement modElement = new ModElement(workspace, "TestTrigger" + externalTrigger.getID(),
					ModElementType.PROCEDURE);

			Procedure procedure = new Procedure(modElement);

			if (externalTrigger.dependencies_provided != null) {
				procedure.getModElement().putMetadata("dependencies", externalTrigger.dependencies_provided);
				procedure.skipDependencyRegeneration();
			}

			int additionalBlocks = 0;
			final StringBuilder additionalXML = new StringBuilder();
			if (externalTrigger.has_result) {
				additionalXML.append(
						"<next><block type=\"set_event_result\"><field name=\"result\">DENY</field>");
				additionalBlocks++;
			} else if (externalTrigger.cancelable) {
				additionalXML.append("<next><block type=\"cancel_event\">");
				additionalBlocks++;
			}

			List<DataListEntry> eventparameters = DataListLoader.loadDataList("eventparameters");
			GeneratorWrapper generatorWrapper = new GeneratorWrapper(workspace.getGenerator());
			for (DataListEntry entry : eventparameters) {
				String parameter = entry.getName();
				String requiredGlobalTrigger = generatorWrapper.map(parameter, "eventparameters", 2);
				if (requiredGlobalTrigger.equals(externalTrigger.getID())) {
					String type = entry.getType();
					if (type.equals("number")) {
						additionalXML.append("""
								<next><block type="event_number_parameter_set">
									<field name="eventparameter">%s</field>
									<value name="value">
										<block type="math_number">
											<field name="NUM">1.234</field>
										</block>
									</value>
								""".formatted(parameter));
						additionalBlocks++;
					} else if (type.equals("logic")) {
						additionalXML.append("""
								<next><block type="event_logic_parameter_set">
									<field name="eventparameter">%s</field>
									<value name="value">
										<block type="logic_boolean">
											<field name="BOOL">TRUE</field>
										</block>
									</value>
								""".formatted(parameter));
						additionalBlocks++;
					}
				}
			}

			additionalXML.append("</block></next>".repeat(additionalBlocks));

			procedure.procedurexml = """
					<xml xmlns="https://developers.google.com/blockly/xml">
						<block type="event_trigger">
							<field name="trigger">%s</field>
							%s
						</block>
					</xml>
					""".formatted(externalTrigger.getID(), additionalXML.toString());

			try {
				workspace.addModElement(modElement);
				workspace.getGenerator().generateElement(procedure, true);
				workspace.getModElementManager().storeModElement(procedure);
			} catch (Throwable t) {
				fail("[" + generatorName + "] Failed generating external trigger: " + externalTrigger.getID(), t);
			}
		}

	}

}
