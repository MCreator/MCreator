/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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
import net.mcreator.element.parts.EffectEntry;
import net.mcreator.minecraft.DataListLoader;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ModElementReference;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused") public class Potion extends GeneratableElement {

	public String potionName;
	public String splashName;
	public String lingeringName;
	public String arrowName;
	@ModElementReference public List<CustomEffectEntry> effects;

	private Potion() {
		this(null);
	}

	public Potion(ModElement element) {
		super(element);
		effects = new ArrayList<>();
	}

	public static class CustomEffectEntry {
		public EffectEntry effect;
		public int duration;
		public int amplifier;
		public boolean ambient;
		public boolean showParticles;

		public int getAmplifier() {
			return amplifier;
		}

		public boolean doesShowParticles() {
			return showParticles;
		}

		public int getLiquidColor(Workspace workspace) {
			if (effect.getUnmappedValue().contains("CUSTOM:")) {
				ModElement modElement = workspace.getModElementByName(effect.getUnmappedValue().replace("CUSTOM:", ""));
				if (modElement != null) {
					GeneratableElement generatableElement = modElement.getGeneratableElement();
					if (generatableElement instanceof PotionEffect) {
						return ((PotionEffect) generatableElement).color.getRGB();
					}
				}
			} else {
				if (DataListLoader.loadDataMap("effects").containsKey(effect.getUnmappedValue())) {
					return Integer.parseInt(
							DataListLoader.loadDataMap("effects").get(effect.getUnmappedValue()).getTexture());
				}
			}
			return 0;
		}

	}

	@Override public BufferedImage generateModElementPicture() {
		return MinecraftImageGenerator.Preview.generatePotionIcon(getPotionColor());
	}

	private Color getPotionColor() {
		if (effects.isEmpty()) {
			return new Color(3694022);
		}
		float r = 0;
		float g = 0;
		float b = 0;
		int count = 0;

		for (CustomEffectEntry effectinstance : effects) {
			if (effectinstance.doesShowParticles()) {
				int k = effectinstance.getLiquidColor(getModElement().getWorkspace());
				int l = effectinstance.getAmplifier() + 1;
				r += (float) (l * (k >> 16 & 255)) / 255.0F;
				g += (float) (l * (k >> 8 & 255)) / 255.0F;
				b += (float) (l * (k & 255)) / 255.0F;
				count += l;
			}
		}

		if (count == 0) {
			return Color.black;
		} else {
			r = r / (float) count * 255.0F;
			g = g / (float) count * 255.0F;
			b = b / (float) count * 255.0F;
			return new Color((int) r << 16 | (int) g << 8 | (int) b);
		}
	}

}