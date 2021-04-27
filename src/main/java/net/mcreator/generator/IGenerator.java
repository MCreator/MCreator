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

import net.mcreator.generator.mapping.MappingLoader;

import java.io.File;

public interface IGenerator extends IGeneratorProvider {

	default File getSourceRoot() {
		return GeneratorUtils.getSourceRoot(getWorkspace(), getGeneratorConfiguration());
	}

	default File getResourceRoot() {
		return GeneratorUtils.getResourceRoot(getWorkspace(), getGeneratorConfiguration());
	}

	default File getModAssetsRoot() {
		return GeneratorUtils.getModAssetsRoot(getWorkspace(), getGeneratorConfiguration());
	}

	default File getModDataRoot() {
		return GeneratorUtils.getModDataRoot(getWorkspace(), getGeneratorConfiguration());
	}

	default MappingLoader getMappings() {
		return getGeneratorConfiguration().getMappingLoader();
	}

	default String getGeneratorMinecraftVersion() {
		return getGeneratorConfiguration().getGeneratorMinecraftVersion();
	}

	default String getGeneratorBuildFileVersion() {
		return getGeneratorConfiguration().getGeneratorBuildFileVersion();
	}

	default String getFullGeneratorVersion() {
		if (getGeneratorConfiguration().getGeneratorSubVersion() == null)
			return getGeneratorBuildFileVersion();

		return getGeneratorBuildFileVersion() + "/" + getGeneratorConfiguration().getGeneratorSubVersion();
	}

}
