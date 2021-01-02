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

package net.mcreator.element.types;

import net.mcreator.element.NamespacedGeneratableElement;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.workspace.elements.ModElement;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Locale;

@SuppressWarnings("unused") public class Recipe extends NamespacedGeneratableElement {

	public String recipeType;
	public double xpReward;
	public int cookingTime;
	public int recipeRetstackSize;
	public String group;

	// Crafting recipe
	public boolean recipeShapeless;
	public MItemBlock[] recipeSlots;
	public MItemBlock recipeReturnStack;

	// Smelting recipe
	public MItemBlock smeltingInputStack;
	public MItemBlock smeltingReturnStack;

	// Blasting recipe
	public MItemBlock blastingInputStack;
	public MItemBlock blastingReturnStack;

	// Smoking recipe
	public MItemBlock smokingInputStack;
	public MItemBlock smokingReturnStack;

	// Stone cutting recipe
	public MItemBlock stoneCuttingInputStack;
	public MItemBlock stoneCuttingReturnStack;

	// Campfire cooking recipe
	public MItemBlock campfireCookingInputStack;
	public MItemBlock campfireCookingReturnStack;

	// Smithing recipe
	public MItemBlock smithingInputStack;
	public MItemBlock smithingInputAdditionStack;
	public MItemBlock smithingReturnStack;

	// Brewing recipe
	public MItemBlock brewingInputStack;
	public MItemBlock brewingIngredientStack;
	public MItemBlock brewingReturnStack;

	private Recipe() {
		this(null);
	}

	public Recipe(ModElement element) {
		super(element);

		this.optimisedRecipe = new OptimisedRecipe(this);

		this.recipeRetstackSize = 1;
		this.namespace = "mod";

		this.cookingTime = 200;
	}

	@Override public void setModElement(ModElement element) {
		super.setModElement(element);

		// for workspaces before 2020.1
		if (this.name == null)
			this.name = element.getName().toLowerCase(Locale.ENGLISH).replaceAll("[^a-z0-9/._-]+", "");
	}

	@Override public BufferedImage generateModElementPicture() {
		BufferedImage mod = null;
		if ("Crafting".equals(recipeType) && !recipeReturnStack.isEmpty()) {
			mod = MinecraftImageGenerator.Preview
					.generateRecipePreviewPicture(getModElement().getWorkspace(), recipeSlots, recipeReturnStack);
		} else if ("Smelting".equals(recipeType) && !smeltingInputStack.isEmpty() && !smeltingReturnStack.isEmpty()) {
			mod = MinecraftImageGenerator.Preview.generateRecipePreviewPicture(getModElement().getWorkspace(),
					new MItemBlock[] { smeltingInputStack }, smeltingReturnStack);
		} else if ("Blasting".equals(recipeType) && !blastingInputStack.isEmpty() && !blastingReturnStack.isEmpty()) {
			mod = MinecraftImageGenerator.Preview
					.generateBlastingPreviewPicture(getModElement().getWorkspace(), blastingInputStack,
							blastingReturnStack);
		} else if ("Smoking".equals(recipeType) && !smokingInputStack.isEmpty() && !smokingReturnStack.isEmpty()) {
			mod = MinecraftImageGenerator.Preview
					.generateSmokingPreviewPicture(getModElement().getWorkspace(), smokingInputStack,
							smokingReturnStack);
		} else if ("Stone cutting".equals(recipeType) && !stoneCuttingInputStack.isEmpty() && !stoneCuttingReturnStack
				.isEmpty()) {
			mod = MinecraftImageGenerator.Preview
					.generateStoneCuttingPreviewPicture(getModElement().getWorkspace(), stoneCuttingInputStack,
							stoneCuttingReturnStack);
		} else if ("Campfire cooking".equals(recipeType) && !campfireCookingInputStack.isEmpty()
				&& !campfireCookingReturnStack.isEmpty()) {
			mod = MinecraftImageGenerator.Preview
					.generateCampfirePreviewPicture(getModElement().getWorkspace(), campfireCookingInputStack,
							campfireCookingReturnStack);
		} else if ("Smithing".equals(recipeType) && !smithingInputStack.isEmpty() && !smithingInputAdditionStack
				.isEmpty() && !smithingReturnStack.isEmpty()) {
			mod = MinecraftImageGenerator.Preview
					.generateSmithingPreviewPicture(getModElement().getWorkspace(), smithingInputStack,
							smithingInputAdditionStack, smithingReturnStack);
		} else if ("Brewing".equals(recipeType) && !brewingInputStack.isEmpty()
				&& !brewingIngredientStack.isEmpty() && !brewingReturnStack.isEmpty()) {
			mod = MinecraftImageGenerator.Preview
					.generateBrewingPreviewPicture(getModElement().getWorkspace(), brewingInputStack,
							brewingIngredientStack, brewingReturnStack);
		}
		return mod;
	}

	private final transient OptimisedRecipe optimisedRecipe;

	public MItemBlock[][] getOptimisedRecipe() {
		return optimisedRecipe.getOptimisedRecipe();
	}

	private static class OptimisedRecipe {

		private final Recipe recipe;

		OptimisedRecipe(Recipe recipe) {
			this.recipe = recipe;
		}

		MItemBlock[][] getOptimisedRecipe() {
			MItemBlock[][] mtx = { { recipe.recipeSlots[0], recipe.recipeSlots[1], recipe.recipeSlots[2] },
					{ recipe.recipeSlots[3], recipe.recipeSlots[4], recipe.recipeSlots[5] },
					{ recipe.recipeSlots[6], recipe.recipeSlots[7], recipe.recipeSlots[8] } };
			int cmin = mtx[0].length;
			int rmin = mtx.length;
			int cmax = -1;
			int rmax = -1;
			for (int r = 0; r < mtx.length; r++)
				for (int c = 0; c < mtx[0].length; c++)
					if (!mtx[r][c].isEmpty()) {
						if (cmin > c)
							cmin = c;
						if (cmax < c)
							cmax = c;
						if (rmin > r)
							rmin = r;
						if (rmax < r)
							rmax = r;
					}
			return trim(mtx, rmin, rmax, cmin, cmax);
		}

		private MItemBlock[][] trim(MItemBlock[][] mtx, int rmin, int rmax, int cmin, int cmax) {
			MItemBlock[][] result = new MItemBlock[rmax - rmin + 1][];
			for (int r = rmin, i = 0; r <= rmax; r++, i++)
				result[i] = Arrays.copyOfRange(mtx[r], cmin, cmax + 1);
			return result;
		}

	}

}
