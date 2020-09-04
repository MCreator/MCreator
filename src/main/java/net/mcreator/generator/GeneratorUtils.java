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

package net.mcreator.generator;

import net.mcreator.workspace.Workspace;

import java.io.File;

public class GeneratorUtils {

	public static File getSourceRoot(Workspace workspace, GeneratorConfiguration generatorConfiguration) {
		return new File(GeneratorTokens.replaceTokens(workspace, generatorConfiguration.getSourceRoot()));
	}

	public static File getResourceRoot(Workspace workspace, GeneratorConfiguration generatorConfiguration) {
		return new File(GeneratorTokens.replaceTokens(workspace, generatorConfiguration.getResourceRoot()));
	}

	public static File getModAssetsRoot(Workspace workspace, GeneratorConfiguration generatorConfiguration) {
		return new File(GeneratorTokens.replaceTokens(workspace, generatorConfiguration.getModAssetsRoot()));
	}

	public static File getModDataRoot(Workspace workspace, GeneratorConfiguration generatorConfiguration) {
		return new File(GeneratorTokens.replaceTokens(workspace, generatorConfiguration.getModDataRoot()));
	}

	public static File getSpecificRoot(Workspace workspace, GeneratorConfiguration generatorConfiguration,
			String root) {
		String rootString = generatorConfiguration.getSpecificRoot(root);
		if (rootString != null)
			return new File(GeneratorTokens.replaceTokens(workspace, rootString));
		else
			return null;
	}
}
