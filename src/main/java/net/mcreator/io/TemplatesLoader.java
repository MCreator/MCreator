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

package net.mcreator.io;

import net.mcreator.plugin.PluginLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TemplatesLoader {

	private static final Logger LOG = LogManager.getLogger(TemplatesLoader.class);

	public static List<ResourcePointer> loadTemplates(String templatePackage, String extension) {
		try {
			Set<String> fileNames = PluginLoader.INSTANCE
					.getResources("templates." + templatePackage, Pattern.compile(".*\\." + extension));
			List<String> templatesSorted = new ArrayList<>(fileNames);
			templatesSorted.sort(String::compareToIgnoreCase);

			List<ResourcePointer> templates = templatesSorted.stream().map(ResourcePointer::new)
					.collect(Collectors.toList());

			UserFolderManager.getFileFromUserFolder("/templates/" + templatePackage.replace(".", "/")).mkdirs();

			File[] customTemplates = UserFolderManager
					.getFileFromUserFolder("/templates/" + templatePackage.replace(".", "/")).listFiles();
			if (customTemplates != null) {
				templates.addAll(Arrays.stream(customTemplates).map(ResourcePointer::new).collect(Collectors.toList()));
			}

			return templates;
		} catch (Exception e) {
			LOG.warn("Failed to load templates", e);
			return Collections.emptyList();
		}
	}

}
