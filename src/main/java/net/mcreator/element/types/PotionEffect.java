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
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.io.FileIO;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.TextureReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

@SuppressWarnings("unused") public class PotionEffect extends GeneratableElement {

	private static final Logger LOG = LogManager.getLogger(PotionEffect.class);

	public String effectName;
	@TextureReference(TextureType.EFFECT) public TextureHolder icon;
	public Color color;
	public boolean isInstant;
	public boolean isBad;
	public boolean isBenefitical;
	public boolean renderStatusInInventory;
	public boolean renderStatusInHUD;

	public Procedure onStarted;
	public Procedure onActiveTick;
	public Procedure onExpired;
	public Procedure activeTickCondition;

	public PotionEffect(ModElement element) {
		super(element);
	}

	@Override public BufferedImage generateModElementPicture() {
		return MinecraftImageGenerator.Preview.generatePotionEffectIcon(icon.getImage(TextureType.EFFECT));
	}

	@Override public void finalizeModElementGeneration() {
		try {
			File newLocation = new File(
					getModElement().getWorkspace().getFolderManager().getTexturesFolder(TextureType.EFFECT),
					getModElement().getRegistryName() + ".png");
			FileIO.copyFile(icon.toFile(TextureType.EFFECT), newLocation);
		} catch (Exception e) {
			LOG.error("Failed to copy potion effect icon", e);
		}
	}

	public boolean hasCustomRenderer() {
		return !renderStatusInHUD || !renderStatusInInventory;
	}
}
