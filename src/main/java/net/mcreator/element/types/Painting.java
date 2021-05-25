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
import net.mcreator.io.FileIO;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.workspace.elements.ModElement;
import org.apache.commons.io.FilenameUtils;

import java.awt.image.BufferedImage;
import java.io.File;

public class Painting extends GeneratableElement {

	public String texture;
	public int width;
	public int height;

	public Painting(ModElement element) {
		super(element);
	}

	@Override public BufferedImage generateModElementPicture() {
		return MinecraftImageGenerator.Preview.generatePaintingPreviewPicture(
				getModElement().getFolderManager().getTextureFileFromType(FilenameUtils.removeExtension(texture), "other"), width,
				height);
	}

	@Override public void finalizeModElementGeneration() {
		File originalTextureFileLocation = getModElement().getFolderManager()
				.getTextureFileFromType(FilenameUtils.removeExtension(texture), "other");
		File newLocation = new File(getModElement().getFolderManager().getTexturesDirFromType("other"),
				"painting/" + getModElement().getRegistryName() + ".png");
		FileIO.copyFile(originalTextureFileLocation, newLocation);
	}
}
