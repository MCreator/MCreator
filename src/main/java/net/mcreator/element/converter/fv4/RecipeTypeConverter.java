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

package net.mcreator.element.converter.fv4;

import com.google.gson.JsonElement;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.types.Recipe;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecipeTypeConverter implements IConverter {

	private static final Logger LOG = LogManager.getLogger("RecipeTypeConverter");

	@Override
	public GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput) {
		Recipe recipe = (Recipe) input;
		try {
			if (jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject().get("recipeReturnStack") != null
					&& !jsonElementInput.getAsJsonObject().get("definition").getAsJsonObject().get("recipeReturnStack")
					.getAsJsonObject().get("value").getAsString().trim().equals("")) { // treat as crafting
				recipe.recipeType = "Crafting";
			} else { // treat as smelting
				recipe.recipeType = "Smelting";
			}
		} catch (Exception e) {
			LOG.warn("Could not determine recipe type for " + input.getModElement().getName()
					+ ", falling back to crafting type.");
			recipe.recipeType = "Crafting";
		}
		return recipe;
	}

	@Override public int getVersionConvertingTo() {
		return 4;
	}

}
