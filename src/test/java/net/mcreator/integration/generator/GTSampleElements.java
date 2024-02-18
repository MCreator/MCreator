/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.integration.TestWorkspaceDataProvider;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GTSampleElements {

	public static void provideAndGenerateSampleElements(Random random, Workspace workspace) {
		// add sample procedures (used by test mod elements) if supported
		if (workspace.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.PROCEDURE)
				!= GeneratorStats.CoverageStatus.NONE) {
			for (int i = 1; i <= 15; i++) {
				ModElement me = new ModElement(workspace, "procedure" + i, ModElementType.PROCEDURE);
				if (i == 1) {
					me.putMetadata("dependencies", Arrays.asList(
							Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity")));
				} else {
					me.putMetadata("dependencies",
							Arrays.asList(Dependency.fromString("x:number/y:number/z:number/world:world")));
				}

				net.mcreator.element.types.Procedure procedure = new net.mcreator.element.types.Procedure(me);
				procedure.skipDependencyRegeneration();
				procedure.procedurexml = GTProcedureBlocks.wrapWithBaseTestXML("");

				addGeneratableElementAndAssert(workspace, procedure);
			}

			for (int i = 1; i <= 4; i++) {
				ModElement me = new ModElement(workspace, "condition" + i, ModElementType.PROCEDURE).putMetadata(
						"return_type", "LOGIC");
				me.putMetadata("dependencies",
						Arrays.asList(Dependency.fromString("x:number/y:number/z:number/world:world")));

				net.mcreator.element.types.Procedure procedure = new net.mcreator.element.types.Procedure(me);
				procedure.procedurexml = GTProcedureBlocks.wrapWithBaseTestXML(
						"<block type=\"return_logic\"><value name=\"return\">"
								+ "<block type=\"logic_boolean\"><field name=\"BOOL\">FALSE</field></block>"
								+ "</value></block>");

				addGeneratableElementAndAssert(workspace, procedure);
			}

			for (int i = 1; i <= 3; i++) {
				ModElement me = new ModElement(workspace, "number" + i, ModElementType.PROCEDURE).putMetadata(
						"return_type", "NUMBER");
				if (i == 3) {
					me.putMetadata("dependencies", Collections.emptyList());
				} else {
					me.putMetadata("dependencies",
							Arrays.asList(Dependency.fromString("x:number/y:number/z:number/world:world")));
				}

				net.mcreator.element.types.Procedure procedure = new net.mcreator.element.types.Procedure(me);
				procedure.skipDependencyRegeneration();
				procedure.procedurexml = GTProcedureBlocks.wrapWithBaseTestXML(
						"<block type=\"return_number\"><value name=\"return\">"
								+ "<block type=\"math_number\"><field name=\"NUM\">100</field></block>"
								+ "</value></block>");

				addGeneratableElementAndAssert(workspace, procedure);
			}

			for (int i = 1; i <= 4; i++) {
				ModElement me = new ModElement(workspace, "string" + i, ModElementType.PROCEDURE).putMetadata(
						"return_type", "STRING");
				me.putMetadata("dependencies",
						Arrays.asList(Dependency.fromString("x:number/y:number/z:number/world:world")));

				net.mcreator.element.types.Procedure procedure = new net.mcreator.element.types.Procedure(me);
				procedure.skipDependencyRegeneration();
				procedure.procedurexml = GTProcedureBlocks.wrapWithBaseTestXML(
						"<block type=\"return_string\"><value name=\"return\">"
								+ "<block type=\"text\"><field name=\"TEXT\">demo text</field></block>"
								+ "</value></block>");

				addGeneratableElementAndAssert(workspace, procedure);
			}

			for (int i = 1; i <= 2; i++) {
				ModElement me = new ModElement(workspace, "itemstack" + i, ModElementType.PROCEDURE).putMetadata(
						"return_type", "ITEMSTACK");
				me.putMetadata("dependencies",
						Arrays.asList(Dependency.fromString("x:number/y:number/z:number/world:world")));

				net.mcreator.element.types.Procedure procedure = new net.mcreator.element.types.Procedure(me);
				procedure.skipDependencyRegeneration();
				procedure.procedurexml = GTProcedureBlocks.wrapWithBaseTestXML(
						"<block type=\"return_itemstack\"><value name=\"return\">"
								+ "<block type=\"empty_itemstack\"></block></value></block>");

				addGeneratableElementAndAssert(workspace, procedure);
			}

			for (int i = 1; i <= 1; i++) {
				ModElement me = new ModElement(workspace, "actionresulttype" + i, ModElementType.PROCEDURE).putMetadata(
						"return_type", "ACTIONRESULTTYPE");
				me.putMetadata("dependencies",
						Arrays.asList(Dependency.fromString("x:number/y:number/z:number/world:world")));

				net.mcreator.element.types.Procedure procedure = new net.mcreator.element.types.Procedure(me);
				procedure.skipDependencyRegeneration();
				procedure.procedurexml = GTProcedureBlocks.wrapWithBaseTestXML(
						"<block type=\"return_actionresulttype\"><value name=\"return\">"
								+ "<block type=\"action_result_type\"><field name=\"type\">SUCCESS</field></block>"
								+ "</value></block>");

				addGeneratableElementAndAssert(workspace, procedure);
			}

			for (int i = 1; i <= 1; i++) {
				ModElement me = new ModElement(workspace, "entity" + i, ModElementType.PROCEDURE).putMetadata(
						"return_type", "ENTITY");
				me.putMetadata("dependencies",
						Arrays.asList(Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity")));

				net.mcreator.element.types.Procedure procedure = new net.mcreator.element.types.Procedure(me);
				procedure.skipDependencyRegeneration();
				procedure.procedurexml = GTProcedureBlocks.wrapWithBaseTestXML(
						"<block type=\"return_entity\"><value name=\"return\">"
								+ "<block type=\"entity_from_deps\"></block></value></block>");

				addGeneratableElementAndAssert(workspace, procedure);
			}
		}

		// add sample recipes (used by test mod elements) if supported
		if (workspace.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.RECIPE)
				!= GeneratorStats.CoverageStatus.NONE) {
			for (int i = 1; i <= 2; i++) {
				ModElement me = new ModElement(workspace, "ExampleRecipe" + i, ModElementType.RECIPE);

				net.mcreator.element.types.Recipe recipe = new net.mcreator.element.types.Recipe(me);
				recipe.recipeType = "smelting";
				recipe.smeltingInputStack = new MItemBlock(workspace,
						TestWorkspaceDataProvider.getRandomMCItem(random, ElementUtil.loadBlocksAndItems(workspace))
								.getName());
				recipe.smeltingReturnStack = new MItemBlock(workspace,
						TestWorkspaceDataProvider.getRandomMCItem(random, ElementUtil.loadBlocksAndItems(workspace))
								.getName());
				recipe.name = me.getRegistryName();
				recipe.namespace = "mod";

				addGeneratableElementAndAssert(workspace, recipe);
			}
		}

		// add sample functions (used by test mod elements) if supported
		if (workspace.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.FUNCTION)
				!= GeneratorStats.CoverageStatus.NONE) {
			for (int i = 1; i <= 2; i++) {
				ModElement me = new ModElement(workspace, "ExampleFunction" + i, ModElementType.FUNCTION);

				net.mcreator.element.types.Function function = new net.mcreator.element.types.Function(me);
				function.code = "";
				function.name = me.getRegistryName();
				function.namespace = "mod";

				addGeneratableElementAndAssert(workspace, function);
			}
		}

		// add sample loot tables (used by test mod elements) if supported
		if (workspace.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.LOOTTABLE)
				!= GeneratorStats.CoverageStatus.NONE) {
			for (int i = 1; i <= 2; i++) {
				ModElement me = new ModElement(workspace, "ExampleLootTable" + i, ModElementType.LOOTTABLE);

				net.mcreator.element.types.LootTable lootTable = new net.mcreator.element.types.LootTable(me);
				lootTable.type = "Generic";
				lootTable.name = me.getRegistryName();
				lootTable.namespace = "mod";
				lootTable.pools = Collections.emptyList();

				addGeneratableElementAndAssert(workspace, lootTable);
			}
		}
	}

	private static void addGeneratableElementAndAssert(Workspace workspace, GeneratableElement generatableElement) {
		workspace.addModElement(generatableElement.getModElement());
		assertTrue(workspace.getGenerator().generateElement(generatableElement));
		workspace.getModElementManager().storeModElement(generatableElement);
	}

}
