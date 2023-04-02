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

import net.mcreator.element.BaseType;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.types.interfaces.ICommonType;
import net.mcreator.element.types.interfaces.IMCItemProvider;
import net.mcreator.element.types.interfaces.ITabContainedElement;
import net.mcreator.io.FileIO;
import net.mcreator.minecraft.MCItem;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.io.File;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unused") public class BannerPattern extends GeneratableElement
		implements ICommonType, ITabContainedElement, IMCItemProvider {

	public String bannerTexture;
	public String shieldTexture;
	public String title;
	public String description;
	public String texture;
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

	@Override public TabEntry getCreativeTab() {
		return creativeTab;
	}

	@Override public Collection<BaseType> getBaseTypesProvided() {
		return List.of(BaseType.ITEM);
	}

	@Override public List<MCItem> providedMCItems() {
		return List.of(new MCItem.Custom(this.getModElement(), null, "item", "Banner Pattern"));
	}

	@Override public List<MCItem> getCreativeTabItems() {
		return List.of(new MCItem.Custom(this.getModElement(), null, "item", "Banner Pattern"));
	}

	@Override public ImageIcon getIconForMCItem(Workspace workspace, String suffix) {
		return workspace.getFolderManager().getTextureImageIcon(texture, TextureType.ITEM);
	}
}