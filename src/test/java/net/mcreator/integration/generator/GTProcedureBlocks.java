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
import net.mcreator.element.types.Procedure;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.integration.TestWorkspaceDataProvider;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.fail;

public class GTProcedureBlocks {

	public static void runTest(Logger LOG, String generatorName, Random random, Workspace workspace) {
		// silently skip if procedures are not supported by this generator
		if (workspace.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.PROCEDURE)
				== GeneratorStats.CoverageStatus.NONE) {
			return;
		}

		Set<String> generatorBlocks = workspace.getGeneratorStats().getBlocklyBlocks(BlocklyEditorType.PROCEDURE);

		for (ToolboxBlock procedureBlock : BlocklyLoader.INSTANCE.getBlockLoader(BlocklyEditorType.PROCEDURE)
				.getDefinedBlocks().values()) {
			StringBuilder additionalXML = new StringBuilder();

			if (!BlocklyTestUtil.validateToolboxBlock(procedureBlock, generatorBlocks, workspace))
				continue; // block is not supported by this generator

			if (!BlocklyTestUtil.validateInputs(procedureBlock))
				continue; // failed to validate inputs

			if (!BlocklyTestUtil.populateFields(procedureBlock, workspace, random, additionalXML))
				continue; // failed to populate all fields

			if (procedureBlock.getStatements() != null) {
				for (StatementInput statement : procedureBlock.getStatements()) {
					additionalXML.append("<statement name=\"").append(statement.name).append("\">")
							.append("<block type=\"text_print\"><value name=\"TEXT\"><block type=\"math_number\">"
									+ "<field name=\"NUM\">123.456</field></block></value></block>")
							.append("</statement>\n");
				}
			}

			if (procedureBlock.getRepeatingStatements() != null) {
				JsonArray args0 = procedureBlock.getBlocklyJSON().get("args0").getAsJsonArray();
				for (int i = 0; i < args0.size(); i++) {
					if (args0.get(i).getAsJsonObject().get("type").getAsString().equals("input_statement")) {
						String name = args0.get(i).getAsJsonObject().get("name").getAsString();
						for (StatementInput statement : procedureBlock.getRepeatingStatements()) {
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

			ModElement modElement = new ModElement(workspace, "TestProcedureBlock" + procedureBlock.getMachineName(),
					ModElementType.PROCEDURE);

			String testXML = procedureBlock.getToolboxTestXML();

			// replace common math blocks with blocks that contain double variable to verify things like type casting
			testXML = testXML.replace("<block type=\"coord_x\"></block>",
					"<block type=\"variables_get_number\"><field name=\"VAR\">local:test</field></block>");
			testXML = testXML.replace("<block type=\"coord_y\"></block>",
					"<block type=\"variables_get_number\"><field name=\"VAR\">local:test</field></block>");
			testXML = testXML.replace("<block type=\"coord_z\"></block>",
					"<block type=\"variables_get_number\"><field name=\"VAR\">local:test</field></block>");
			testXML = testXML.replaceAll("<block type=\"math_number\"><field name=\"NUM\">(.*?)</field></block>",
					"<block type=\"variables_get_number\"><field name=\"VAR\">local:test</field></block>");

			// replace common logic blocks with blocks that contain logic variable
			testXML = testXML.replace("<block type=\"logic_boolean\"><field name=\"BOOL\">TRUE</field></block>",
					"<block type=\"variables_get_logic\"><field name=\"VAR\">local:flag</field></block>");
			testXML = testXML.replace("<block type=\"logic_boolean\"><field name=\"BOOL\">FALSE</field></block>",
					"<block type=\"variables_get_logic\"><field name=\"VAR\">local:flag</field></block>");

			// add additional xml to the block definition
			testXML = testXML.replace("<block type=\"" + procedureBlock.getMachineName() + "\">",
					"<block type=\"" + procedureBlock.getMachineName() + "\">" + additionalXML);

			// replace common itemstack blocks with blocks that contain local variable
			testXML = testXML.replace("<block type=\"itemstack_to_mcitem\"></block>",
					"<block type=\"variables_get_itemstack\"><field name=\"VAR\">local:stackvar</field></block>");
			testXML = testXML.replace("<block type=\"mcitem_all\"><field name=\"value\"></field></block>",
					"<block type=\"variables_get_itemstack\"><field name=\"VAR\">local:stackvar</field></block>");

			// set MCItem blocks to some value
			testXML = testXML.replace("<block type=\"mcitem_allblocks\"><field name=\"value\"></field></block>",
					"<block type=\"mcitem_allblocks\"><field name=\"value\">"
							+ TestWorkspaceDataProvider.getRandomMCItem(random,
							ElementUtil.loadBlocks(modElement.getWorkspace())).getName() + "</field></block>");

			Procedure procedure = new Procedure(modElement);

			if (procedureBlock.getType() == IBlockGenerator.BlockType.PROCEDURAL) {
				procedure.procedurexml = wrapWithBaseTestXML(testXML);
			} else { // output block type
				String rettype = procedureBlock.getOutputTypeForTests();
				switch (rettype) {
				case "Number":
					procedure.procedurexml = wrapWithBaseTestXML("""
							<block type="return_number"><value name="return">
								<block type="math_dual_ops">
									<field name="OP">ADD</field>
									<value name="A">%s</value>
									<value name="B">%s</value>
								</block>
							</value></block>
							""".formatted(testXML, testXML));
					break;
				case "Boolean":
					procedure.procedurexml = wrapWithBaseTestXML("""
							<block type="return_logic"><value name="return">
								<block type="logic_binary_ops">
									<field name="OP">OR</field>
									<value name="A">%s</value>
									<value name="B">%s</value>
								</block>
							</value></block>
							""".formatted(testXML, testXML));
					break;
				case "ProjectileEntity": // Projectile blocks are tested with the "Shoot from entity" procedure
					procedure.procedurexml = wrapWithBaseTestXML("""
							<block type="projectile_shoot_from_entity">
								<value name="projectile">%s</value>
								<value name="entity"><block type="entity_from_deps"></block></value>
								<value name="speed"><block type="math_number"><field name="NUM">1</field></block></value>
								<value name="inaccuracy"><block type="math_number"><field name="NUM">0</field></block></value>
							</block>""".formatted(testXML));
					break;
				default:
				case "String":
					procedure.procedurexml = wrapWithBaseTestXML("""
							<block type="return_string"><value name="return">
								<block type="text_join">
									<mutation items="2"></mutation>
									<value name="ADD0">%s</value>
									<value name="ADD1">%s</value>
								</block>
							</value></block>
							""".formatted(testXML, testXML));
					break;
				}
			}

			try {
				workspace.addModElement(modElement);
				workspace.getGenerator().generateElement(procedure, true);
				workspace.getModElementManager().storeModElement(procedure);
			} catch (Throwable t) {
				t.printStackTrace();
				fail("[" + generatorName + "] Failed generating procedure block: " + procedureBlock.getMachineName());
			}
		}

	}

	public static String wrapWithBaseTestXML(String customXML) {
		return "<xml xmlns=\"https://developers.google.com/blockly/xml\">" + "<variables>"
				+ "<variable type=\"Number\" id=\"test\">test</variable>"
				+ "<variable type=\"Boolean\" id=\"flag\">flag</variable>"
				+ "<variable type=\"MCItem\" id=\"stackvar\">stackvar</variable>" + "</variables>"
				+ "<block type=\"event_trigger\" deletable=\"false\" x=\"59\" y=\"38\">"
				+ "<field name=\"trigger\">no_ext_trigger</field>" + "<next><block type=\"variables_set_logic\">"
				+ "<field name=\"VAR\">local:flag</field><value name=\"VAL\"><block type=\"logic_negate\">"
				+ "<value name=\"BOOL\"><block type=\"variables_get_logic\"><field name=\"VAR\">local:flag</field>"
				+ "</block></value></block></value><next><block type=\"variables_set_number\">"
				+ "<field name=\"VAR\">local:test</field><value name=\"VAL\"><block type=\"math_dual_ops\">"
				+ "<field name=\"OP\">ADD</field><value name=\"A\"><block type=\"variables_get_number\">"
				+ "<field name=\"VAR\">local:test</field></block></value><value name=\"B\"><block type=\"math_number\">"
				+ "<field name=\"NUM\">1.23</field></block></value></block></value><next><block type=\"variables_set_itemstack\">"
				+ "<field name=\"VAR\">local:stackvar</field><value name=\"VAL\"><block type=\"mcitem_all\"><field name=\"value\">"
				+ "Blocks.STONE</field></block></value><next>" + customXML
				+ "</next></block></next></block></next></block></next></block></xml>";
	}

}
