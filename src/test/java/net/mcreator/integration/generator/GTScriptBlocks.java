/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

import com.google.gson.JsonArray;
import net.mcreator.blockly.IBlockGenerator;
import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.blockly.data.StatementInput;
import net.mcreator.blockly.data.ToolboxBlock;
import net.mcreator.element.ModElementType;
import net.mcreator.element.types.bedrock.BEScript;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.fail;

public class GTScriptBlocks {

	public static void runTest(Logger LOG, String generatorName, Random random, Workspace workspace) {
		Set<String> generatorBlocks = workspace.getGeneratorStats().getBlocklyBlocks(BlocklyEditorType.SCRIPT);

		for (ToolboxBlock scriptBlock : BlocklyLoader.INSTANCE.getBlockLoader(BlocklyEditorType.SCRIPT)
				.getDefinedBlocks().values()) {
			StringBuilder additionalXML = new StringBuilder();

			if (!BlocklyTestUtil.validateToolboxBlock(scriptBlock, generatorBlocks, workspace))
				continue; // block is not supported by this generator

			if (!BlocklyTestUtil.validateInputs(scriptBlock))
				continue; // failed to validate inputs

			if (!BlocklyTestUtil.populateFields(scriptBlock, workspace, random, additionalXML))
				continue; // failed to populate all fields

			if (scriptBlock.getStatements() != null) {
				for (StatementInput statement : scriptBlock.getStatements()) {
					additionalXML.append("<statement name=\"").append(statement.name).append("\">")
							.append("<block type=\"text_print\"><value name=\"TEXT\"><block type=\"math_number\">"
									+ "<field name=\"NUM\">123.456</field></block></value></block>")
							.append("</statement>\n");
				}
			}

			if (scriptBlock.getRepeatingStatements() != null) {
				JsonArray args0 = scriptBlock.getBlocklyJSON().get("args0").getAsJsonArray();
				for (int i = 0; i < args0.size(); i++) {
					if (args0.get(i).getAsJsonObject().get("type").getAsString().equals("input_statement")) {
						String name = args0.get(i).getAsJsonObject().get("name").getAsString();
						for (StatementInput statement : scriptBlock.getRepeatingStatements()) {
							if (name.matches(statement.name + "\\d+")) {
								additionalXML.append("<statement name=\"").append(name).append("\">")
										.append("<block type=\"text_print\"><value name=\"TEXT\">"
												+ "<block type=\"math_number\"><field name=\"NUM\">123.456</field>")
										.append("</block></value></block></statement>\n");
							}
						}
					}
				}
			}

			String testXML = scriptBlock.getToolboxTestXML();

			// add additional xml to the block definition
			testXML = testXML.replace("<block type=\"" + scriptBlock.getMachineName() + "\">",
					"<block type=\"" + scriptBlock.getMachineName() + "\">" + additionalXML);

			prepareTestCase(workspace, generatorName, scriptBlock.getMachineName(), testXML, scriptBlock.getType(),
					scriptBlock.getOutputType());
		}
	}

	private static void prepareTestCase(Workspace workspace, String generatorName, String testCaseName, String testXML,
			IBlockGenerator.BlockType blockType, @Nullable String rettype) {
		ModElement modElement = new ModElement(workspace, "TestScriptBlock" + testCaseName, ModElementType.BESCRIPT);

		BEScript beScript = new BEScript(modElement);

		if (blockType == IBlockGenerator.BlockType.PROCEDURAL) {
			String inner = StringUtils.substringBeforeLast(testXML, "</block>");
			String doubledXML = inner + "<next>" + testXML + "</next></block>";
			beScript.scriptxml = wrapWithBaseTestXML(doubledXML);
		} else {
			beScript.scriptxml = wrapWithBaseTestXML("""
					<block type="text_print"><value name="TEXT">
						<block type="text_join">
							<mutation items="2"></mutation>
							<value name="ADD0">%s</value>
							<value name="ADD1">%s</value>
						</block>
					</value></block>
					""".formatted(testXML, testXML));
		}

		try {
			workspace.addModElement(modElement);
			workspace.getGenerator().generateElement(beScript, true);
			workspace.getModElementManager().storeModElement(beScript);
		} catch (Throwable t) {
			fail("[" + generatorName + "] Failed generating script block: " + testCaseName, t);
		}
	}

	public static String wrapWithBaseTestXML(String customXML) {
		return """
				<xml xmlns="https://developers.google.com/blockly/xml">
				  <block type="script_trigger" deletable="false" x="59" y="38">
				    <field name="trigger">player_ticks</field>
				    <next>
					%s
				  	</next>
				  </block>
				</xml>
				""".formatted(customXML);
	}

}
