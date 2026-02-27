/*
 * MCreator (https://mcreator.net/)
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

package net.mcreator.element.types.bedrock;

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.workspace.elements.ModElement;

import java.awt.Color;
import java.awt.image.BufferedImage;

@SuppressWarnings("unused") public class BEBiome extends GeneratableElement {

	public String name;

	public MItemBlock topMaterial;
	public MItemBlock midMaterial;
	public MItemBlock foundationMaterial;
	public MItemBlock seaFloorMaterial;
	public MItemBlock seaMaterial;

	public int seaFloorDepth;

	public Color airColor;
	public Color fogColor;
	public Color grassColor;
	public Color foliageColor;
	public Color waterColor;
	public Color waterFogColor;

	public double temperature;
	public double downfall;

	public boolean generateFeature;
	public String noiseType;

	private BEBiome() {
		this(null);
	}

	public BEBiome(ModElement element) {
		super(element);
	}

	public MItemBlock getUndergroundBlock() {
		if (foundationMaterial == null || foundationMaterial.isEmpty())
			return midMaterial;

		return foundationMaterial;
	}

	public MItemBlock getUnderwaterBlock() {
		if (seaFloorMaterial == null || seaFloorMaterial.isEmpty())
			return midMaterial;

		return seaFloorMaterial;
	}

	public MItemBlock getOceanBlock() {
		if (seaMaterial == null || seaMaterial.isEmpty())
			return new MItemBlock(getModElement().getWorkspace(), "Blocks.WATER");

		return seaMaterial;
	}

	public boolean hasFog() {
		return fogColor != null || waterFogColor != null;
	}

	@Override public BufferedImage generateModElementPicture() {
		return MinecraftImageGenerator.Preview.generateBiomePreviewPicture(getModElement().getWorkspace(), fogColor,
				new Color(9421151), waterColor, topMaterial, midMaterial, 0, 0, null, null);
	}
}
