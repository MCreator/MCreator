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

package net.mcreator.element.types;

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.io.FileIO;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;

import java.io.File;

public class BannerPattern extends GeneratableElement {

	public String bannerTexture;
	public String shieldTexture;
	public String title;
	public String description;
	public TabEntry creativeTab;

	public BannerPattern(ModElement element) {
		super(element);
	}

	@Override public void finalizeModElementGeneration() {
		Workspace workspace = getModElement().getWorkspace();
		String workspaceRoot = workspace.getWorkspaceFolder().getAbsolutePath();
		File vanillaTextureFolder = new File(workspaceRoot, "src/main/resources/assets/minecraft/textures");

		File originalBannerTextureFileLocation = getModElement().getFolderManager()
				.getTextureFile(FilenameUtilsPatched.removeExtension(bannerTexture), TextureType.OTHER);
		File newBannerTextureFileLocation = new File(vanillaTextureFolder,
				"entity/banner/" + getModElement().getRegistryName() + ".png");
		FileIO.copyFile(originalBannerTextureFileLocation, newBannerTextureFileLocation);

		File originalShieldTextureFileLocation = getModElement().getFolderManager()
				.getTextureFile(FilenameUtilsPatched.removeExtension(shieldTexture), TextureType.OTHER);
		File newShieldTextureFileLocation = new File(vanillaTextureFolder,
				"entity/shield/" + getModElement().getRegistryName() + ".png");
		FileIO.copyFile(originalShieldTextureFileLocation, newShieldTextureFileLocation);
	}
}