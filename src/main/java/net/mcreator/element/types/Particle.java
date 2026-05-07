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

import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.TextureHolder;
import net.mcreator.element.parts.procedure.NumberProcedure;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.types.interfaces.Numeric;
import net.mcreator.element.types.interfaces.LimitedOptions;
import net.mcreator.io.FileIO;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.util.image.InvalidTileSizeException;
import net.mcreator.util.image.TiledImageUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.TextureReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@SuppressWarnings("unused") public class Particle extends GeneratableElement {

	private static final Logger LOG = LogManager.getLogger(Particle.class);

	@TextureReference(TextureType.PARTICLE) public TextureHolder texture;

	public boolean animate;
	@Numeric(init = 1, min = 1, max = 100000, step = 1) public int frameDuration;

	@Numeric(init = 0.2, min = 0, max = 4096, step = 0.1) public double width;
	@Numeric(init = 0.2, min = 0, max = 4096, step = 0.1) public double height;
	public NumberProcedure scale;
	public boolean fixedScale;
	@Numeric(init = 1, min = -100, max = 100, step = 0.1) public double speedFactor;
	@Numeric(init = 0, min = -100, max = 100, step = 0.1) public double gravity;
	@Numeric(init = 7, min = 0, max = 100000, step = 1) public int maxAge;
	@Numeric(init = 0, min = 0, max = 100000, step = 1) public int maxAgeDiff;
	@Numeric(init = 0, min = -100, max = 100, step = 0.01) public double angularVelocity;
	@Numeric(init = 0, min = -100, max = 100, step = 0.01) public double angularAcceleration;

	public boolean canCollide;
	public boolean alwaysShow;
	public boolean emissiveRendering;
	public Procedure rotationProvider;

	@LimitedOptions({ "OPAQUE", "TRANSLUCENT" }) public String renderType;

	public Procedure additionalExpiryCondition;

	private Particle() {
		this(null);
	}

	public Particle(ModElement element) {
		super(element);
	}

	public int getTextureTileCount() {
		try {
			ImageIcon original = new ImageIcon(texture.toFile(TextureType.PARTICLE).getAbsolutePath());
			if (original.getImage() != null && original.getIconWidth() > 0 && original.getIconHeight() > 0) {
				if (original.getIconWidth() >= original.getIconHeight()
						|| original.getIconHeight() % original.getIconWidth() != 0)
					return 1;
				return original.getIconHeight() / original.getIconWidth();
			}
		} catch (Exception e) {
			LOG.error("Failed to get texture tile count", e);
		}
		return 1;
	}

	@Override public void finalizeModElementGeneration() {
		try {
			File originalTextureFileLocation = texture.toFile(TextureType.PARTICLE);

			ImageIcon original = new ImageIcon(originalTextureFileLocation.getAbsolutePath());

			if (original.getImage() != null && original.getIconWidth() > 0 && original.getIconHeight() > 0) {
				if (original.getIconWidth() >= original.getIconHeight()
						|| original.getIconHeight() % original.getIconWidth() != 0) {
					FileIO.copyFile(originalTextureFileLocation,
							new File(getModElement().getFolderManager().getTexturesFolder(TextureType.PARTICLE),
									getModElement().getRegistryName() + ".png"));
				} else {
					try {
						TiledImageUtils tiu = new TiledImageUtils(ImageUtils.toBufferedImage(original.getImage()),
								original.getIconWidth(), original.getIconWidth());
						int tiles = getTextureTileCount();
						for (int i = 1; i <= tiles; i++) {
							ImageIO.write(ImageUtils.toBufferedImage(tiu.getIcon(1, i).getImage()), "png",
									new File(getModElement().getFolderManager().getTexturesFolder(TextureType.PARTICLE),
											getModElement().getRegistryName() + "_" + i + ".png"));
						}
					} catch (InvalidTileSizeException | IOException ignored) {
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Failed to generate particle texture files", e);
		}
	}

	@Override public BufferedImage generateModElementPicture() {
		return MinecraftImageGenerator.Preview.generateParticlePreviewPicture(texture.getImage(TextureType.PARTICLE),
				getTextureTileCount() > 1, getModElement().getName());
	}

	public boolean hasAngularVelocityOrAcceleration() {
		return angularVelocity != 0 || angularAcceleration != 0 || (rotationProvider != null
				&& rotationProvider.getDependencies(this.getModElement().getWorkspace()).stream().map(Dependency::name)
				.anyMatch(name -> "angularVelocity".equals(name) || "angularAcceleration".equals(name)));
	}

}
