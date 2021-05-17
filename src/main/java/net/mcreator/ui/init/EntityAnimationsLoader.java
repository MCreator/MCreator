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

package net.mcreator.ui.init;

import com.google.gson.Gson;
import net.mcreator.io.FileIO;
import net.mcreator.plugin.PluginLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gradle.internal.FileUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class EntityAnimationsLoader {

	private static final Logger LOG = LogManager.getLogger("Entity Animations Loader");

	private static final LinkedHashMap<String, String[]> entityAnimations = new LinkedHashMap<>();

	public static void init() {
		LOG.debug("Loading entity animations");

		Set<String> fileNames = PluginLoader.INSTANCE
				.getResources("templates.animations", Pattern.compile("^[^$].*\\.json"));

		// We add "No animation" directly as it does not contain animations
		entityAnimations.put("No animation", new String[] {});

		final Gson gson = new Gson();

		for (String file : fileNames) {
			String[] animationCodes = gson
					.fromJson(FileIO.readResourceToString(PluginLoader.INSTANCE, file), String[].class);
			entityAnimations.put(FileUtils.removeExtension(file.replace("templates/animations/", "")), animationCodes);
		}
	}

	public static List<String> getAnimationIDs() {
		return new ArrayList<>(entityAnimations.keySet());
	}

	public static String[] getAnimationCodesFromID(String animationID) {
		return entityAnimations.get(animationID);
	}

}
