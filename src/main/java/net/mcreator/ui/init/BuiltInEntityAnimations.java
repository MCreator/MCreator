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

import java.util.*;
import java.util.regex.Pattern;

public class BuiltInEntityAnimations {

	private static final Logger LOG = LogManager.getLogger("Entity Animations loader");

	private static final LinkedHashMap<String, List<String>> entityAnimations = new LinkedHashMap<>();

	public static void loadEntityAnimations() {
		LOG.debug("Loading entity animations");

		final Gson gson = new Gson();

		Set<String> fileNames = PluginLoader.INSTANCE
				.getResources("templates.animations", Pattern.compile("^[^$].*\\.json"));

		// We add No animation directly as it does not contain animations
		entityAnimations.put(L10N.t("animations.entities.no_anim"), Collections.emptyList());
		for (String file : fileNames) {
			EntityAnimation anim = gson
					.fromJson(FileIO.readResourceToString(PluginLoader.INSTANCE, file), EntityAnimation.class)
					.setId(FileUtils.removeExtension(file).replace("templates/animations/", ""));
			entityAnimations.put(anim.getText(), anim.getAnimations());
		}
	}

	public static Set<String> getAllTexts() {
		return entityAnimations.keySet();
	}

	public static List<String> getAnimations(String key) {
		return entityAnimations.get(key);
	}

	public static class EntityAnimation implements Comparator<EntityAnimation> {
		private final List<String> animations = new ArrayList<>();
		private String id;

		public EntityAnimation setId(String id) {
			this.id = id;
			return this;
		}

		public String getText() {
			return L10N.t("animations.entities." + id);
		}

		public List<String> getAnimations() {
			return animations;
		}

		@Override public String toString() {
			return getText() + ": " + getAnimations();
		}

		@Override public int compare(EntityAnimation o1, EntityAnimation o2) {
			return 0;
		}
	}
}
