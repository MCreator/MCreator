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
import net.mcreator.io.FileIO;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.ui.dialogs.tools.quickrecipestool.QuickRecipesTool;
import net.mcreator.ui.dialogs.tools.quickrecipestool.RecipeTemplate;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.IntegerRange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.regex.Pattern;

public class RecipeTemplatesLoader {
	private static final Logger LOG = LogManager.getLogger("Recipe Templates Loader");

	private static final LinkedHashMap<String, RecipeTemplate> recipeTemplates = new LinkedHashMap<>();

	public static void init() {
		LOG.debug("Loading recipe templates");

		Set<String> fileNames = PluginLoader.INSTANCE.getResources("templates.recipe_templates",
				Pattern.compile("^[^$].*\\.json$"));

		final Gson gson = new Gson();

		for (String file : fileNames) {
			String name = FilenameUtils.removeExtension(file.replace("templates/recipe_templates/", ""));

			RecipeTemplate recipeTemplate = gson.fromJson(FileIO.readResourceToString(PluginLoader.INSTANCE, file),
					RecipeTemplate.class);

			if (recipeTemplate.recipeType == null || !QuickRecipesTool.SUPPORTED_RECIPE_TYPES.contains(
					recipeTemplate.recipeType)) {
				LOG.error("Recipe type {} of recipe template file {} is not supported. It will be skipped.",
						recipeTemplate.recipeType, name);
				continue;
			}

			if (!recipeTemplate.recipeType.equals("Crafting") && Arrays.stream(recipeTemplate.inputSlots)
					.anyMatch(i -> !IntegerRange.of(0, 8).contains(i))) {
				LOG.error("Recipe template file {} contains a slot outside of the range (0-8). It will be skipped.",
						name);
				continue;
			}

			if (!IntegerRange.of(1, 99).contains(recipeTemplate.stackSize)) {
				LOG.error(
						"Stack size {} of recipe template file {} is outside of the range (1-99). It will be skipped.",
						recipeTemplate.stackSize, name);
				continue;
			}

			recipeTemplates.put(name, recipeTemplate);
		}
	}

	public static List<String> getRecipeTemplates() {
		return new ArrayList<>(recipeTemplates.keySet());
	}

	public static RecipeTemplate getRecipeTemplatesFromID(String templateID) {
		return recipeTemplates.get(templateID);
	}

}