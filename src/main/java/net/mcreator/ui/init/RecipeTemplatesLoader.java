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

package net.mcreator.ui.init;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.mcreator.io.FileIO;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.ui.dialogs.tools.quickrecipestool.QuickRecipesTool;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.IntegerRange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.regex.Pattern;

public class RecipeTemplatesLoader {
	private static final Logger LOG = LogManager.getLogger("Recipe Templates Loader");

	private static final LinkedHashMap<String, RecipeTemplate> recipeTemplates = new LinkedHashMap<>();

	public static void init() {
		LOG.debug("Loading recipe templates");

		Set<String> fileNames = PluginLoader.INSTANCE.getResources("templates.recipe_templates",
				Pattern.compile("^[^$].*\\.json$"));

		final Gson gson = new Gson();

		for (String file : fileNames.stream().sorted().toList()) {
			String name = FilenameUtils.removeExtension(file.replace("templates/recipe_templates/", ""));

			try {
				RecipeTemplate recipeTemplate = gson.fromJson(FileIO.readResourceToString(PluginLoader.INSTANCE, file),
						RecipeTemplate.class);

				recipeTemplate.selfValidate();
				recipeTemplates.put(name, recipeTemplate);
			} catch (JsonSyntaxException e) {
				LOG.error("Recipe template format of {} is invalid. It will be skipped. {}", name, e.getMessage());
			} catch (IllegalArgumentException e) {
				LOG.error("Recipe template {} contains one or many invalid parameters. It will be skipped. {}", name,
						e.getMessage());
			}
		}
	}

	public static String[] getTemplateNames() {
		return recipeTemplates.keySet().toArray(new String[0]);
	}

	public static RecipeTemplate getRecipeTemplatesFromID(String templateID) {
		return recipeTemplates.get(templateID);
	}

	public static class RecipeTemplate {
		public int stackSize;
		public int[] inputSlots = new int[] {};
		public boolean isShapeless;
		public String recipeType;
		@Nullable public String craftingBookCategory;

		public void selfValidate() throws IllegalArgumentException {
			if (recipeType == null || !QuickRecipesTool.SUPPORTED_RECIPE_TYPES.contains(recipeType)) {
				throw new IllegalArgumentException("Invalid recipe type: " + recipeType);
			}

			if (recipeType.equals("Crafting") && Arrays.stream(inputSlots)
					.anyMatch(i -> !IntegerRange.of(0, 8).contains(i))) {
				throw new IllegalArgumentException("One or many slots are outside the valid range (0-8)");
			}

			if (!IntegerRange.of(1, 99).contains(stackSize)) {
				throw new IllegalArgumentException("Invalid stack size: " + stackSize);
			}
		}
	}
}