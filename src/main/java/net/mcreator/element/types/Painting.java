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
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.ModElement;
import org.apache.commons.io.FilenameUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class Painting extends GeneratableElement {

	public String texture;
	public int width;
	public int height;

	public Painting(ModElement element) {
		super(element);
	}


	@Override public void finalizeModElementGeneration() {
		File originalTextureFileLocation = getModElement().getWorkspace().getFolderManager()
				.getOtherTextureFile(FilenameUtils.removeExtension(texture));
		File newLocation = new File(getModElement().getWorkspace().getFolderManager().getOtherTexturesDir(),
				"painting/" + getModElement().getRegistryName() + ".png");
		FileIO.copyFile(originalTextureFileLocation, newLocation);
	}
}
