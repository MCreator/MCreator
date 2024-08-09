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
import java.lang.module.ModuleDescriptor;

public abstract class AbstractFolderStructure {

	protected final Workspace workspace;

	protected AbstractFolderStructure(Workspace workspace) {
		this.workspace = workspace;
	}

	@Nullable public abstract File getStructuresDir();

	@Nullable public abstract File getSoundsDir();

	@Nullable public abstract File getTexturesFolder(TextureType section);

	@Nullable public abstract File getSourceRoot();

	@Nullable public abstract File getResourceRoot();

	public static AbstractFolderStructure getFolderStructure(Workspace workspace) {
		// if the current generator of the workspace is defined, we just use the current folder structure of that generator
		if (workspace.getGenerator() != null)
			return new CurrentFolderStructure(workspace);

		String[] currentGeneratorData = workspace.getWorkspaceSettings().getCurrentGenerator().split("-");
		//String flavor = currentGeneratorData[0];
		String minecraftVersion = currentGeneratorData[1];

		ModuleDescriptor.Version currentVersion = ModuleDescriptor.Version.parse(minecraftVersion);

		if (currentVersion.compareTo(ModuleDescriptor.Version.parse("1.19.3")) < 0) {
			return new Pre1193FolderStructure(workspace);
		} else if (currentVersion.compareTo(ModuleDescriptor.Version.parse("1.21")) < 0) {
			return new Pre1210FolderStructure(workspace);
		}

		// if we fail to detect suitable folder structure, we just use the current folder structure
		return new CurrentFolderStructure(workspace);
	}

}
