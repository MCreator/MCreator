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
import net.mcreator.element.types.bedrock.BEScript;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.Logger;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.fail;

public class GTScriptTriggers {

	public static void runTest(Logger LOG, String generatorName, Workspace workspace) {
		Set<String> generatorTriggers = workspace.getGeneratorStats().getBlocklyTriggers(BlocklyEditorType.SCRIPT);

		for (ExternalTrigger externalTrigger : BlocklyLoader.INSTANCE.getExternalTriggerLoader(
				BlocklyEditorType.SCRIPT).getExternalTriggers()) {
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
					ModElementType.BESCRIPT);

			BEScript beScript = new BEScript(modElement);

			int additionalBlocks = 0;
			final StringBuilder additionalXML = new StringBuilder();
			if (externalTrigger.cancelable) {
				additionalXML.append("<next><block type=\"cancel_event\">");
				additionalBlocks++;
			}

			additionalXML.append("</block></next>".repeat(additionalBlocks));

			beScript.scriptxml = """
					<xml xmlns="https://developers.google.com/blockly/xml">
						<block type="script_trigger">
							<field name="trigger">%s</field>
							%s
						</block>
					</xml>
					""".formatted(externalTrigger.getID(), additionalXML.toString());

			try {
				workspace.addModElement(modElement);
				workspace.getGenerator().generateElement(beScript, true);
				workspace.getModElementManager().storeModElement(beScript);
			} catch (Throwable t) {
				fail("[" + generatorName + "] Failed generating external trigger: " + externalTrigger.getID(), t);
			}
		}

	}

}
