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

package net.mcreator.ui.laf;

import net.mcreator.ui.init.UIRES;

import javax.swing.*;
import java.io.File;

public class FileIcons {

	public static ImageIcon getIconForFile(File file) {
		return getIconForFile(file.getName(), file.isDirectory());
	}

	public static ImageIcon getIconForFile(String file, boolean folder) {
		if (folder)
			return UIRES.get("laf.directory");

		if (file.endsWith(".java"))
			return UIRES.get("16px.class");
		if (file.endsWith(".ogg"))
			return UIRES.get("16px.sound");
		if (file.equals("mcmod.info") || file.endsWith(".json") || file.endsWith(".mcmeta"))
			return UIRES.get("16px.json");
		if (file.endsWith(".yaml") || file.endsWith(".yml"))
			return UIRES.get("16px.yaml");
		if (file.endsWith(".gradle"))
			return UIRES.get("16px.gradle");
		if (file.endsWith(".txt"))
			return UIRES.get("laf.text");
		if (file.endsWith(".png"))
			return UIRES.get("laf.image");

		return UIRES.get("laf.file");
	}

}
