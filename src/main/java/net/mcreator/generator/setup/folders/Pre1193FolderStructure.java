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

class Pre1193FolderStructure extends AbstractFolderStructure {

	protected Pre1193FolderStructure(Workspace workspace) {
		super(workspace);
	}

	@Nullable @Override public File getStructuresDir() {
		return new File(getResourceRoot(), "data/" + workspace.getWorkspaceSettings().getModID() + "/structures");
	}

	@Nullable @Override public File getSoundsDir() {
		return new File(getResourceRoot(), "assets/" + workspace.getWorkspaceSettings().getModID() + "/sounds");
	}

	@Nullable @Override public File getTexturesFolder(TextureType section) {
		return switch (section) {
			case BLOCK -> new File(getResourceRoot(),
					"assets/" + workspace.getWorkspaceSettings().getModID() + "/textures/blocks");
			case ITEM -> new File(getResourceRoot(),
					"assets/" + workspace.getWorkspaceSettings().getModID() + "/textures/items");
			case ARMOR -> new File(getResourceRoot(),
					"assets/" + workspace.getWorkspaceSettings().getModID() + "/textures/models/armor");
			case OTHER ->
					new File(getResourceRoot(), "assets/" + workspace.getWorkspaceSettings().getModID() + "/textures");
			// The types below may not exist on older generators with shared folder for all but block, item and armor,
			// but this will be taken care of by converters from other texture type section
			case ENTITY -> new File(getResourceRoot(),
					"assets/" + workspace.getWorkspaceSettings().getModID() + "/textures/entities");
			case EFFECT -> new File(getResourceRoot(),
					"assets/" + workspace.getWorkspaceSettings().getModID() + "/textures/mob_effect");
			case PARTICLE -> new File(getResourceRoot(),
					"assets/" + workspace.getWorkspaceSettings().getModID() + "/textures/particle");
			case SCREEN -> new File(getResourceRoot(),
					"assets/" + workspace.getWorkspaceSettings().getModID() + "/textures/screens");
		};
	}

	@Nullable @Override public File getSourceRoot() {
		return new File(workspace.getWorkspaceFolder(), "src/main/java");
	}

	@Nullable @Override public File getResourceRoot() {
		return new File(workspace.getWorkspaceFolder(), "src/main/resources");
	}

}
