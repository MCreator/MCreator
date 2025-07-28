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

import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.Workspace;

import javax.annotation.Nullable;
import java.io.File;

/**
 * Before 1.21.0, structures folder is in plural form
 */
class Pre1210FolderStructure extends AbstractFolderStructure {

	protected Pre1210FolderStructure(Workspace workspace) {
		super(workspace);
	}

	@Nullable @Override public File getStructuresDir() {
		return new File(getResourceRoot(), "data/" + workspace.getWorkspaceSettings().getModID() + "/structures");
	}

	@Nullable @Override public File getSoundsDir() {
		return new File(getResourceRoot(), "assets/" + workspace.getWorkspaceSettings().getModID() + "/sounds");
	}

	@Nullable @Override public File getTexturesFolder(TextureType section) {
		return new File(getResourceRoot(), "assets/" + workspace.getWorkspaceSettings().getModID() + switch (section) {
			case BLOCK -> "/textures/block";
			case ITEM -> "/textures/item";
			case ENTITY -> "/textures/entities";
			case ARMOR -> "/textures/models/armor";
			case OTHER -> "/textures";
			case EFFECT -> "/textures/mob_effect";
			case PARTICLE -> "/textures/particle";
			case SCREEN -> "/textures/screens";
		});
	}

	@Nullable @Override public File getSourceRoot() {
		return new File(workspace.getWorkspaceFolder(), "src/main/java");
	}

	@Nullable @Override public File getResourceRoot() {
		return new File(workspace.getWorkspaceFolder(), "src/main/resources");
	}

}
