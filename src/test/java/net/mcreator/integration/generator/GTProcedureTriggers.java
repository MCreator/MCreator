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
import net.mcreator.generator.GeneratorStats;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.Logger;

import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GTProcedureTriggers {

	public static void runTest(Logger LOG, String generatorName, Workspace workspace) {
		if (workspace.getGenerator().getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.PROCEDURE)
				== GeneratorStats.CoverageStatus.NONE) {
			LOG.warn("[" + generatorName
					+ "] Skipping procedure triggers test as the current generator does not support them.");
			return;
		}

		Set<String> generatorTriggers = workspace.getGenerator().getGeneratorStats().getGeneratorTriggers();

		for (ExternalTrigger externalTrigger : BlocklyLoader.INSTANCE.getExternalTriggerLoader()
				.getExternalTrigers()) {
			if (!generatorTriggers.contains(externalTrigger.getID())) {
				LOG.warn("[" + generatorName + "] Skipping procedure trigger that is not defined by generator: "
						+ externalTrigger.getID());
				continue;
			}

			if (externalTrigger.required_apis != null) {
				boolean skip = false;

				for (String required_api : externalTrigger.required_apis) {
					if (!workspace.getWorkspaceSettings().getMCreatorDependencies()
							.contains(required_api)) {
						skip = true;
						break;
					}
				}

				if (skip)  {
					LOG.warn("[" + generatorName + "] Skipping API specific procedure trigger: "
							+ externalTrigger.getID());
					continue;
				}
			}

			ModElement modElement = new ModElement(workspace, "Test" + externalTrigger.getID(),
					ModElementType.PROCEDURE);

			Procedure procedure = new Procedure(modElement);
			procedure.procedurexml =
					"<xml xmlns=\"https://developers.google.com/blockly/xml\"><block type=\"event_trigger\" deletable=\"false\" x=\"40\" y=\"40\"><field name=\"trigger\">"
							+ externalTrigger.getID() + "</field></block></xml>";

			try {
				workspace.addModElement(modElement);
				assertTrue(workspace.getGenerator().generateElement(procedure));
				workspace.getModElementManager().storeModElement(procedure);
			} catch (Throwable t) {
				fail("[" + generatorName + "] Failed generating external trigger: " + externalTrigger.getID());
				t.printStackTrace();
			}
		}

	}

}
