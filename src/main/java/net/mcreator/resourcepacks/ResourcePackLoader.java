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

package net.mcreator.resourcepacks;

import com.google.gson.Gson;
import net.mcreator.io.FileIO;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.init.UIRES;
import net.mcreator.util.image.ImageUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class ResourcePackLoader {
	private static final Logger LOG = LogManager.getLogger("Resource Pack Loader");

	private static final LinkedHashSet<ResourcePack> resourcePacks = new LinkedHashSet<>();

	public static void initResourcePacks() {
		LOG.debug("Loading resource packs");

		final Gson gson = new Gson();

		Set<String> files = PluginLoader.INSTANCE.getResources("resourcepacks", Pattern.compile("pack.json"));
		for (String file : files) {
			ResourcePack pack = gson
					.fromJson(FileIO.readResourceToString(PluginLoader.INSTANCE, file), ResourcePack.class);
			// The ID will be used to get images from this resource pack if the user selected it.
			pack.setId(new File(file).getParentFile().getName());

			// Load the custom icon if provided otherwise, load the default one
			String identifier = "resourcepacks/" + pack.getID() + "/icon.png";
			if (PluginLoader.INSTANCE.getResource(identifier) != null) {
				ImageIcon icon = UIRES.fromResourceID(identifier);
				icon = new ImageIcon(ImageUtils.resize(icon.getImage(), 64));
				pack.setIcon(icon);
			} else {
				pack.setIcon(UIRES.get("icon.png"));
			}
			resourcePacks.add(pack);
			LOG.debug("Loaded " + pack.getID());
		}
		// We check if the last resource pack selected by the user still exists
		// If the resource pack has been deleted since the last time, we load the default resource pack
		if (ResourcePackLoader.getResourcePack(PreferencesManager.PREFERENCES.hidden.resourcePack) == null) {
			PreferencesManager.PREFERENCES.hidden.resourcePack = "default";
		}
	}

	public static LinkedHashSet<ResourcePack> getResourcePacks() {
		return resourcePacks;
	}

	public static List<String> getIDs() {
		List<String> ids = new ArrayList<>();
		for (ResourcePack rp : resourcePacks) {
			ids.add(rp.getID());
		}
		return ids;
	}

	public static ResourcePack getResourcePack(String id) {
		for (ResourcePack pack : resourcePacks) {
			if (pack.getID().equals(id))
				return pack;
		}
		return null;
	}
}
