/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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
import net.mcreator.element.parts.TextureHolder;
import net.mcreator.io.FileIO;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.TextureReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.io.File;

public class BannerPattern extends GeneratableElement {

	private static final Logger LOG = LogManager.getLogger(BannerPattern.class);

	@TextureReference(TextureType.OTHER) public TextureHolder texture;
	@TextureReference(TextureType.OTHER) public TextureHolder shieldTexture;
	public String name;
	public boolean requireItem;

	public BannerPattern(ModElement element) {
		super(element);
	}

	@Override public BufferedImage generateModElementPicture() {
		return MinecraftImageGenerator.Preview.generateBannerPatternPreviewPicture(texture.getImage(TextureType.OTHER));
	}

	@Override public void finalizeModElementGeneration() {
		try {
			File bannerLocation = new File(getModElement().getFolderManager().getTexturesFolder(TextureType.OTHER),
					"entity/banner/" + getModElement().getRegistryName() + ".png");
			FileIO.copyFile(texture.toFile(TextureType.OTHER), bannerLocation);
			File shieldLocation = new File(getModElement().getFolderManager().getTexturesFolder(TextureType.OTHER),
					"entity/shield/" + getModElement().getRegistryName() + ".png");
			FileIO.copyFile(shieldTexture.toFile(TextureType.OTHER), shieldLocation);
		} catch (Exception e) {
			LOG.error("Failed to copy banner pattern textures", e);
		}
	}
}
