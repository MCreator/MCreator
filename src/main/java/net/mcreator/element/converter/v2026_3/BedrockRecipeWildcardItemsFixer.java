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

package net.mcreator.element.converter.v2026_3;

import com.google.gson.JsonElement;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.types.Recipe;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.workspace.Workspace;

import java.util.Set;

/**
 * Bedrock Edition generator used to map wildcard ("any x") entries listed below to their first variant
 * (e.g. Blocks.STONE to "stone"). These mappings were removed, so this converter rewrites such entries
 * to the explicit first variant (e.g. Blocks.STONE#0) to preserve the previously generated recipes.
 */
public class BedrockRecipeWildcardItemsFixer implements IConverter {

	// Those entries don't have a tag group in Bedrock Edition. This converter rewrites them to the first variant.
	private static final Set<String> REMOVED_WILDCARDS = Set.of("Blocks.STONE", "Blocks.DIRT", "Blocks.SAPLING",
			"Blocks.QUARTZ_BLOCK", "Blocks.LEAVES", "Blocks.SPONGE", "Blocks.SANDSTONE", "Blocks.RED_SANDSTONE",
			"Blocks.RED_FLOWER", "Blocks.DOUBLE_PLANT", "Blocks.MONSTER_EGG", "Blocks.STONE_SLAB", "Blocks.ANVIL",
			"Blocks.COBBLESTONE_WALL", "Blocks.SKULL", "Blocks.STAINED_HARDENED_CLAY", "Blocks.CARPET",
			"Blocks.STAINED_GLASS", "Blocks.STAINED_GLASS_PANE", "Blocks.PRISMARINE", "Blocks.CANDLE",
			"Blocks.CANDLE_CAKE", "Items.GOLDEN_APPLE", "Items.SKULL");

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		if (workspace.getGeneratorConfiguration().getGeneratorFlavor() != GeneratorFlavor.ADDON)
			return input;

		Recipe recipe = (Recipe) input;

		if (recipe.recipeSlots != null) {
			for (int i = 0; i < recipe.recipeSlots.length; i++)
				recipe.recipeSlots[i] = fixWildcard(workspace, recipe.recipeSlots[i]);
		}

		recipe.smeltingInputStack = fixWildcard(workspace, recipe.smeltingInputStack);
		recipe.blastingInputStack = fixWildcard(workspace, recipe.blastingInputStack);
		recipe.smokingInputStack = fixWildcard(workspace, recipe.smokingInputStack);
		recipe.stoneCuttingInputStack = fixWildcard(workspace, recipe.stoneCuttingInputStack);
		recipe.campfireCookingInputStack = fixWildcard(workspace, recipe.campfireCookingInputStack);

		recipe.unlockingItems.replaceAll(item -> fixWildcard(workspace, item));

		return recipe;
	}

	private static MItemBlock fixWildcard(Workspace workspace, MItemBlock item) {
		if (item != null && item.getUnmappedValue() != null && REMOVED_WILDCARDS.contains(item.getUnmappedValue()))
			return new MItemBlock(workspace, item.getUnmappedValue() + "#0");
		return item;
	}

	@Override public int getVersionConvertingTo() {
		return 90;
	}

}
