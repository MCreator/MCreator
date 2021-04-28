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
import com.google.gson.JsonArray;
import net.mcreator.io.FileIO;
import net.mcreator.plugin.PluginLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gradle.internal.FileUtils;

import java.util.*;
import java.util.regex.Pattern;

public class BuiltInEntityAnimations {

	private static final Logger LOG = LogManager.getLogger("Entity Animations loader");

	private static final LinkedHashMap<String, JsonArray> entityAnimations = new LinkedHashMap<>();

	public static void loadEntityAnimations() {
		LOG.debug("Loading entity animations");

		final Gson gson = new Gson();

		Set<String> fileNames = PluginLoader.INSTANCE
				.getResources("templates.animations", Pattern.compile("^[^$].*\\.json"));

		// We add No animation directly as it does not contain animations
		entityAnimations.put("No animation", new JsonArray());
		for (String file : fileNames) {
			// We use a temporary class to save values and get them for the Map
			JsonArray anims = gson
					.fromJson(FileIO.readResourceToString(PluginLoader.INSTANCE, file), JsonArray.class);
			EntityAnimation anim = new EntityAnimation(FileUtils.removeExtension(file).replace("templates/animations/", ""),
					anims);
			entityAnimations.put(anim.getID(), anim.getAnimations());
		}
	}

	public static Set<String> getAllIDs() {
		return entityAnimations.keySet();
	}

	public static JsonArray getAnimations(String key) {
		return entityAnimations.get(key);
	}

	private static class EntityAnimation {
		private final JsonArray animations;
		private final String id;

		public EntityAnimation(String id, JsonArray animations) {
			this.id = id;
			this.animations = animations;
		}

		public String getID() {
			return id;
		}

		public JsonArray getAnimations() {
			return animations;
		}
	}
}
