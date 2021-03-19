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

package net.mcreator.element.types;

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.io.FileIO;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.workspace.elements.ModElement;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.awt.image.BufferedImage;
import java.io.File;

public class Tab extends GeneratableElement {

	public String name;
	public String bgTexture;
	public MItemBlock icon;
	public boolean showSearch;

	public Tab(ModElement element) {
		super(element);
	}

	@Override public BufferedImage generateModElementPicture() {
		return MinecraftImageGenerator.Preview.generateCreativeTabPreviewPicture(getModElement().getWorkspace(), icon);
	}

	@Override public void finalizeModElementGeneration() {
		if (Float.parseFloat(StringUtils
				.removeStart(getModElement().getWorkspace().getGeneratorConfiguration().getGeneratorMinecraftVersion(),
						"1.")) < 16.5) {
			File originalTextureFileLocation = getModElement().getFolderManager()
					.getOtherTextureFile(FilenameUtils.removeExtension(bgTexture));
			File newLocation = new File(getModElement().getFolderManager().getWorkspaceFolder(),
					"src/main/resources/assets/minecraft/textures/gui/container/creative_inventory/tab_" + FilenameUtils
							.removeExtension(bgTexture) + ".png");
			FileIO.copyFile(originalTextureFileLocation, newLocation);
		}
	}
}
