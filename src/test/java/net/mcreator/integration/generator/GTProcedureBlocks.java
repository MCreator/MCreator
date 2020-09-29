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

import net.mcreator.blockly.IBlockGenerator;
import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.blockly.data.ToolboxBlock;
import net.mcreator.element.ModElementType;
import net.mcreator.element.types.Procedure;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.util.ListUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GTProcedureBlocks {

	public static void runTest(Logger LOG, String generatorName, Random random, Workspace workspace) {
		if (workspace.getGenerator().getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.PROCEDURE)
				== GeneratorStats.CoverageStatus.NONE) {
			LOG.warn("[" + generatorName
					+ "] Skipping procedure blocks test as the current generator does not support them.");
			return;
		}

		Set<String> generatorBlocks = workspace.getGenerator().getGeneratorStats().getGeneratorProcedures();

		for (ToolboxBlock procedureBlock : BlocklyLoader.INSTANCE.getProcedureBlockLoader()
				.getDefinedBlocks().values()) {

			if (!generatorBlocks.contains(procedureBlock.machine_name)) {
				LOG.warn("[" + generatorName + "] Skipping procedure block that is not defined by generator: "
						+ procedureBlock.machine_name);
				continue;
			}

			if (procedureBlock.toolboxXML == null) {
				LOG.warn("[" + generatorName + "] Skipping procedure block without default XML defined: "
						+ procedureBlock.machine_name);
				continue;
			}

			if (procedureBlock.fields != null) {
				LOG.warn("[" + generatorName + "] Skipping procedure block with fields (no test atm): "
						+ procedureBlock.machine_name);
				continue;
			}

			if (procedureBlock.statements != null) {
				LOG.warn("[" + generatorName + "] Skipping procedure block with statements (no test atm): "
						+ procedureBlock.machine_name);
				continue;
			}

			if (procedureBlock.inputs != null && procedureBlock.inputs.size() != StringUtils
					.countMatches(procedureBlock.toolboxXML, "<value name")) {
				LOG.warn("[" + generatorName + "] Skipping procedure block with incomplete template (no test atm): "
						+ procedureBlock.machine_name);
				continue;
			}

			if (procedureBlock.required_apis != null) {
				boolean skip = false;

				for (String required_api : procedureBlock.required_apis) {
					if (!workspace.getWorkspaceSettings().getMCreatorDependencies().contains(required_api)) {
						skip = true;
						break;
					}
				}

				if (skip) {
					LOG.warn("[" + generatorName + "] Skipping API specific procedure block: "
							+ procedureBlock.machine_name);
					continue;
				}
			}

			ModElement modElement = new ModElement(workspace, "TestBlock" + procedureBlock.machine_name,
					ModElementType.PROCEDURE);

			String testXML = procedureBlock.toolboxXML;

			// set MCItem block to some value
			testXML = testXML.replace("<block type=\"mcitem_allblocks\"><field name=\"value\"></field></block>",
					"<block type=\"mcitem_allblocks\"><field name=\"value\">" + ListUtils
							.getRandomItem(random, ElementUtil.loadBlocks(modElement.getWorkspace())).getName()
							+ "</field></block>");

			testXML = testXML.replace("<block type=\"mcitem_all\"><field name=\"value\"></field></block>",
					"<block type=\"mcitem_all\"><field name=\"value\">" + ListUtils
							.getRandomItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName()
							+ "</field></block>");

			Procedure procedure = new Procedure(modElement);

			if (procedureBlock.type == IBlockGenerator.BlockType.PROCEDURAL) {
				procedure.procedurexml = "<xml xmlns=\"https://developers.google.com/blockly/xml\">"
						+ "<block type=\"event_trigger\"><field name=\"trigger\">no_ext_trigger</field><next>" + testXML
						+ "</next></block></xml>";
			} else { // output block type
				String rettype = procedureBlock.getOutputType();
				switch (rettype) {
				case "Number":
					procedure.procedurexml = "<xml xmlns=\"https://developers.google.com/blockly/xml\">"
							+ "<block type=\"event_trigger\"><field name=\"trigger\">no_ext_trigger</field><next>"
							+ "<block type=\"return_number\"><value name=\"return\">" + testXML
							+ "</value></block></next></block></xml>";
					break;
				case "Boolean":
					procedure.procedurexml = "<xml xmlns=\"https://developers.google.com/blockly/xml\">"
							+ "<block type=\"event_trigger\"><field name=\"trigger\">no_ext_trigger</field><next>"
							+ "<block type=\"return_logic\"><value name=\"return\">" + testXML
							+ "</value></block></next></block></xml>";
					break;
				case "String":
					procedure.procedurexml = "<xml xmlns=\"https://developers.google.com/blockly/xml\">"
							+ "<block type=\"event_trigger\"><field name=\"trigger\">no_ext_trigger</field><next>"
							+ "<block type=\"return_text\"><value name=\"return\">" + testXML
							+ "</value></block></next></block></xml>";
					break;
				default:
					procedure.procedurexml = "<xml xmlns=\"https://developers.google.com/blockly/xml\">"
							+ "<block type=\"event_trigger\"><field name=\"trigger\">no_ext_trigger</field><next>"
							+ "<block type=\"text_print\"><value name=\"TEXT\">" + testXML
							+ "</value></block></next></block></xml>";
					break;
				}
			}

			try {
				workspace.addModElement(modElement);
				assertTrue(workspace.getGenerator().generateElement(procedure));
				workspace.getModElementManager().storeModElement(procedure);
			} catch (Throwable t) {
				fail("[" + generatorName + "] Failed generating procedure block: " + procedureBlock.machine_name);
				t.printStackTrace();
			}
		}

	}

}
