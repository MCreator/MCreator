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

package net.mcreator.integration.generator;

import net.mcreator.element.ModElementType;
import net.mcreator.element.types.Procedure;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableElement;
import net.mcreator.workspace.elements.VariableType;
import net.mcreator.workspace.elements.VariableTypeLoader;
import org.apache.logging.log4j.Logger;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.fail;

public class GTVariables {

	public static void runTest(Logger LOG, String generatorName, Random random, Workspace workspace) {
		StringBuilder xml = new StringBuilder();
		int blocksToClose = 0;

		// Step 1: Add all variables to the workspace and prepare Blockly XML
		for (VariableType type : VariableTypeLoader.INSTANCE.getGlobalVariableTypes(
				workspace.getGeneratorConfiguration())) {
			int idx = 0;
			for (VariableType.Scope scope : type.getSupportedScopesWithoutLocal(
					workspace.getGeneratorConfiguration())) {
				String varName = type + "" + idx++;

				VariableElement variable = new VariableElement(varName);
				variable.setType(type);
				variable.setScope(scope);
				variable.setValue(type.getDefaultValue(workspace));
				workspace.addVariableElement(variable);

				String extraData = "";
				if (scope == VariableType.Scope.PLAYER_LIFETIME || scope == VariableType.Scope.PLAYER_PERSISTENT) {
					extraData = """
							<mutation is_player_var="true" has_entity="true"></mutation>
							<value name="entity">
							  <block type="entity_from_deps"></block>
							</value>
							""";
				}

				xml.append("""
						<block type="variables_set_%1$s">
						  %3$s
						  <field name="VAR">%2$s</field>
						  <value name="VAL">
						    <block type="variables_get_%1$s">
						      %3$s
						      <field name="VAR">%2$s</field>
						    </block>
						  </value>
						  <next>
						    <block type="text_print">
						      <value name="TEXT">
						        <block type="text_join">
						          <mutation items="2"></mutation>
						          <value name="ADD0">
						            <block type="variables_get_%1$s">
						              %3$s
						              <field name="VAR">%2$s</field>
						            </block>
						          </value>
						          <value name="ADD1">
						            <block type="variables_get_%1$s">
						              %3$s
						              <field name="VAR">%2$s</field>
						            </block>
						          </value>
						        </block>
						      </value>
						      <next>
						""".formatted(type.getName(), "global:" + varName, extraData));

				blocksToClose += 2;
			}
		}
		xml.append("</next></block>".repeat(blocksToClose));

		String procedureXML = wrapWithBaseTestXML(xml.toString());

		try {
			ModElement modElement = new ModElement(workspace, "TestVariables", ModElementType.PROCEDURE);
			Procedure procedure = new Procedure(modElement);
			procedure.procedurexml = procedureXML;

			workspace.addModElement(modElement);
			workspace.getGenerator().generateElement(procedure, true);
			workspace.getModElementManager().storeModElement(procedure);

		} catch (Throwable t) {
			fail("[" + generatorName + "] Failed generating variable test procedure", t);
		}
	}

	private static String wrapWithBaseTestXML(String blocksXML) {
		return """
				<xml xmlns="https://developers.google.com/blockly/xml">
				  <block type="event_trigger" deletable="false" x="20" y="20">
				    <field name="trigger">no_ext_trigger</field>
				    <next>
				      %s
				    </next>
				  </block>
				</xml>
				""".formatted(blocksXML);
	}

}
