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

public class Painting extends GeneratableElement {

	private static final Logger LOG = LogManager.getLogger(Painting.class);

	@TextureReference(TextureType.OTHER) public TextureHolder texture;
	public int width;
	public int height;
	public String title;
	public String author;

	public Painting(ModElement element) {
		super(element);
	}

	@Override public BufferedImage generateModElementPicture() {
		return MinecraftImageGenerator.Preview.generatePaintingPreviewPicture(texture.getImage(TextureType.OTHER),
				width, height);
	}

	@Override public void finalizeModElementGeneration() {
		try {
			File newLocation = new File(getModElement().getFolderManager().getTexturesFolder(TextureType.OTHER),
					"painting/" + getModElement().getRegistryName() + ".png");
			FileIO.copyFile(texture.toFile(TextureType.OTHER), newLocation);
		} catch (Exception e) {
			LOG.error("Failed to copy painting texture", e);
		}
	}

}
