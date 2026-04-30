/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

package net.mcreator.ui.dialogs.tools.quickrecipestool;

import net.mcreator.element.ModElementType;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.types.Recipe;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.BasicAction;
import net.mcreator.ui.dialogs.tools.AbstractPackMakerTool;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.RecipeTemplatesLoader;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.variants.modmaker.ModMaker;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;
import java.util.Arrays;

public class QuickRecipesTool extends AbstractPackMakerTool {

	public static final String[] AVAILABLE_TEMPLATES = new String[] { "Stairs", "Slab", "Wall", "1x1 (Ingots)", "2x2 (Bricks)", "3x3 (Ore block)" };

	private final JRecipeList recipes;

	private QuickRecipesTool(MCreator mcreator) {
		super(mcreator, "quick_recipes", UIRES.get("16px.quickrecipe").getImage());

		recipes = new JRecipeList(mcreator, IHelpContext.NONE, validableElements);

		this.add("Center", recipes);

		this.setSize(1105, 620);
		this.setLocationRelativeTo(mcreator);
		this.setVisible(true);
	}

	@Override protected void generatePack(MCreator mcreator) {
		recipes.getEntries().forEach(
				recipe -> addRecipeToWorkspace(this, mcreator, mcreator.getWorkspace(), recipe.name, recipe.template,
						recipe.input, recipe.result, recipe.generateStonecuttingRecipe));
	}

	public static void addRecipeToWorkspace(@Nullable AbstractPackMakerTool packMaker, MCreator mcreator,
			Workspace workspace, String name, String template, MItemBlock input, MItemBlock result,
			boolean generateStonecuttingRecipe) {

		if (!checkIfNamesAvailable(workspace, name, name + "Stonecutting"))
			return;

		// select folder the mod pack should be in
		FolderElement folder = mcreator instanceof ModMaker modMaker ?
				modMaker.getWorkspacePanel().currentFolder :
				null;

		Recipe recipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name, ModElementType.RECIPE), false).getElementFromGUI();
		recipe.recipeType = "Crafting";
		recipe.craftingBookCategory = "BUILDING";
		recipe.unlockingItems.add(input);
		recipe.recipeReturnStack = result;

		RecipeTemplate recipeTemplate = RecipeTemplatesLoader.getRecipeTemplatesFromID(template);
		recipe.recipeRetstackSize = recipeTemplate.stackSize;
		Arrays.stream(recipeTemplate.inputSlots).forEach(slot -> recipe.recipeSlots[slot] = input);
		if (recipeTemplate.isShapeless)
			recipe.recipeShapeless = true;
		addGeneratableElementToWorkspace(packMaker, workspace, folder, recipe);

		if (generateStonecuttingRecipe) {
			Recipe stonecutter = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
							new ModElement(mcreator.getWorkspace(), name + "Stonecutting", ModElementType.RECIPE), false)
					.getElementFromGUI();
			stonecutter.recipeType = "Stone cutting";
			stonecutter.unlockingItems.add(input);
			stonecutter.stoneCuttingReturnStack = result;
			stonecutter.stoneCuttingInputStack = input;

			addGeneratableElementToWorkspace(packMaker, mcreator.getWorkspace(), folder, stonecutter);
		}
	}

	public static boolean isSupported(GeneratorConfiguration gc) {
		return gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.RECIPE)
				!= GeneratorStats.CoverageStatus.NONE;
	}

	public static BasicAction getAction(ActionRegistry actionRegistry) {
		return new BasicAction(actionRegistry, L10N.t("action.pack_tools.recipe"),
				e -> new QuickRecipesTool(actionRegistry.getMCreator())) {
			@Override public boolean isEnabled() {
				return isSupported(actionRegistry.getMCreator().getGeneratorConfiguration());
			}
		}.setIcon(UIRES.get("16px.quickrecipe"));
	}

}
