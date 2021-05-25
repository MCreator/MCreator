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
import net.mcreator.element.parts.Procedure;
import net.mcreator.io.FileIO;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.util.image.InvalidTileSizeException;
import net.mcreator.util.image.TiledImageUtils;
import net.mcreator.workspace.elements.ModElement;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Particle extends GeneratableElement {

	public String texture;

	public boolean animate;
	public int frameDuration;

	public double width;
	public double height;
	public double scale;
	public double speedFactor;
	public double gravity;
	public int maxAge;
	public int maxAgeDiff;
	public double angularVelocity;
	public double angularAcceleration;

	public boolean canCollide;
	public boolean alwaysShow;

	public String renderType;

	public Procedure additionalExpiryCondition;

	public Particle(ModElement element) {
		super(element);
	}

	public int getTextureTileCount() {
		File originalTextureFileLocation = getModElement().getFolderManager()
				.getTextureFileFromType(FilenameUtils.removeExtension(texture), "particle");
		ImageIcon original = new ImageIcon(originalTextureFileLocation.toString());
		if (original.getImage() != null && original.getIconWidth() > 0 && original.getIconHeight() > 0) {
			if (original.getIconWidth() >= original.getIconHeight()
					|| original.getIconHeight() % original.getIconWidth() != 0)
				return 1;

			return original.getIconHeight() / original.getIconWidth();
		}
		return 1;
	}

	@Override public void finalizeModElementGeneration() {
		File originalTextureFileLocation = getModElement().getFolderManager()
				.getTextureFileFromType(FilenameUtils.removeExtension(texture), "particle");

		ImageIcon original = new ImageIcon(originalTextureFileLocation.toString());

		if (original.getImage() != null && original.getIconWidth() > 0 && original.getIconHeight() > 0) {
			new File(getModElement().getFolderManager().getTexturesDirFromType("particle"), "particle").mkdirs();
			if (original.getIconWidth() >= original.getIconHeight()
					|| original.getIconHeight() % original.getIconWidth() != 0) {
				FileIO.copyFile(originalTextureFileLocation,
						new File(getModElement().getFolderManager().getTexturesDirFromType("particle"),
								"particle/" + getModElement().getRegistryName() + ".png"));
			} else {
				try {
					TiledImageUtils tiu = new TiledImageUtils(ImageUtils.toBufferedImage(original.getImage()),
							original.getIconWidth(), original.getIconWidth());
					int tiles = getTextureTileCount();
					for (int i = 1; i <= tiles; i++) {
						ImageIO.write(ImageUtils.toBufferedImage(tiu.getIcon(1, i).getImage()), "png",
								new File(getModElement().getFolderManager().getTexturesDirFromType("particle"),
										"particle/" + getModElement().getRegistryName() + "_" + i + ".png"));
					}
				} catch (InvalidTileSizeException | IOException ignored) {
				}
			}
		}
	}

	@Override public BufferedImage generateModElementPicture() {
		return MinecraftImageGenerator.Preview.generateParticlePreviewPicture(
				getModElement().getFolderManager().getTextureFileFromType(FilenameUtils.removeExtension(texture), "particle"),
				getTextureTileCount() > 1, getModElement().getName());
	}

}
