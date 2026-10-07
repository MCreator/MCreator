/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.Workspace;

import javax.annotation.Nullable;
import java.io.File;

/**
 * Before add-on generator 26.5x, entity textures folder is in plural form
 */
class Pre265AddonFolderStructure extends AbstractFolderStructure {

	protected Pre265AddonFolderStructure(GeneratorFlavor flavor, Workspace workspace) {
		super(flavor, workspace);
	}

	@Nullable @Override public File getStructuresDir() {
		return new File(getSourceRoot(), "structures/" + workspace.getWorkspaceSettings().getModID());
	}

	@Nullable @Override public File getSoundsDir() {
		return new File(getResourceRoot(), "sounds");
	}

	@Nullable @Override public File getTexturesFolder(TextureType section) {
		return switch (section) {
			case BLOCK -> new File(getResourceRoot(), "textures/blocks");
			case ITEM -> new File(getResourceRoot(), "textures/items");
			case ENTITY -> new File(getResourceRoot(), "textures/entities");
			case OTHER -> new File(getResourceRoot(), "textures");
			default -> null;
		};
	}

	@Nullable @Override public File getSourceRoot() {
		return new File(workspace.getWorkspaceFolder(),
				"src/main/" + workspace.getWorkspaceSettings().getModID() + "_behaviourpack");
	}

	@Nullable @Override public File getResourceRoot() {
		return new File(workspace.getWorkspaceFolder(),
				"src/main/" + workspace.getWorkspaceSettings().getModID() + "_resourcepack");
	}

}
