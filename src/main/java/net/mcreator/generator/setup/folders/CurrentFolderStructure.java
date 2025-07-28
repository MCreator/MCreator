/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.generator.setup.folders;

import net.mcreator.generator.Generator;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.generator.GeneratorUtils;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.Workspace;

import javax.annotation.Nullable;
import java.io.File;

/**
 * This version of the folder structure estimator uses folder structure of the latest supported Forge generator available.
 * <p>
 * If the provided workspace has generator defined, it will use that generator for the folder structure.
 */
class CurrentFolderStructure extends AbstractFolderStructure {

	@Nullable private GeneratorConfiguration generatorConfiguration;

	protected CurrentFolderStructure(Workspace workspace) {
		super(workspace);

		if (workspace.getGenerator()
				== null) // unknown generator, we need to guess based on the latest supported (Neo)Forge generator
			generatorConfiguration = GeneratorConfiguration.getRecommendedGeneratorForFlavor(
					Generator.GENERATOR_CACHE.values(), GeneratorFlavor.FORGE, GeneratorFlavor.NEOFORGE);
	}

	@Nullable @Override public File getStructuresDir() {
		if (workspace.getGenerator() != null) { // known generator, use folder manager
			return workspace.getFolderManager().getStructuresDir();
		} else { // unknown generator (no definition), we need to guess (guess based on the current structure of the latest supported Forge generator)
			if (generatorConfiguration != null)
				return GeneratorUtils.getSpecificRoot(workspace, generatorConfiguration, "structures_dir");

			return null;
		}
	}

	@Nullable @Override public File getSoundsDir() {
		if (workspace.getGenerator() != null) { // known generator, use folder manager
			return workspace.getFolderManager().getSoundsDir();
		} else { // unknown generator (no definition), we need to guess (guess based on the current structure of the latest supported Forge generator)
			if (generatorConfiguration != null)
				return GeneratorUtils.getSpecificRoot(workspace, generatorConfiguration, "sounds_dir");

			return null;
		}
	}

	@Nullable @Override public File getTexturesFolder(TextureType section) {
		if (workspace.getGenerator() != null) { // known generator, use folder manager
			return workspace.getFolderManager().getTexturesFolder(section);
		} else { // unknown generator (no definition), we need to guess (guess based on the current structure of the latest supported Forge generator)
			if (generatorConfiguration != null)
				return GeneratorUtils.getSpecificRoot(workspace, generatorConfiguration,
						section.getID() + "_textures_dir");

			return null;
		}
	}

	@Nullable @Override public File getSourceRoot() {
		if (workspace.getGenerator() != null) { // known generator, use folder manager
			return workspace.getGenerator().getSourceRoot();
		} else { // unknown generator (no definition), we need to guess
			if (generatorConfiguration != null)
				return GeneratorUtils.getSourceRoot(workspace, generatorConfiguration);

			return null;
		}
	}

	@Nullable @Override public File getResourceRoot() {
		if (workspace.getGenerator() != null) { // known generator, use folder manager
			return workspace.getGenerator().getResourceRoot();
		} else { // unknown generator (no definition), we need to guess
			if (generatorConfiguration != null)
				return GeneratorUtils.getResourceRoot(workspace, generatorConfiguration);

			return null;
		}
	}

}
