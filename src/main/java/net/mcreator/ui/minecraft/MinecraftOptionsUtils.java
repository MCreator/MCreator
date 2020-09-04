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

package net.mcreator.ui.minecraft;

import net.mcreator.io.FileIO;
import net.mcreator.workspace.Workspace;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Locale;

public class MinecraftOptionsUtils {

	public static File getOptionsFile(@NotNull Workspace workspace) {
		return new File(workspace.getFolderManager().getWorkspaceFolder(), "run/options.txt");
	}

	public static void setLangTo(@NotNull Workspace workspace, String lang) {
		File optionsFile = getOptionsFile(workspace);
		if (optionsFile.isFile()) {
			String original = FileIO.readFileToString(optionsFile);
			if (original.contains("\nlang:")) {
				original = original.replaceAll("lang:.*", "lang:" + lang.toLowerCase(Locale.ENGLISH));
			} else {
				original += "\nlang:" + lang.toLowerCase(Locale.ENGLISH) + "\n";
			}
			FileIO.writeStringToFile(original, optionsFile);
		}
	}

}
