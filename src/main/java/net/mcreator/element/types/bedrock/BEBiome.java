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
import net.mcreator.element.parts.BiomeEntry;
import net.mcreator.element.parts.BiomeTagEntry;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.parts.Sound;
import net.mcreator.element.types.interfaces.LimitedOptions;
import net.mcreator.element.types.interfaces.Numeric;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ModElementReference;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

@SuppressWarnings("unused") public class BEBiome extends GeneratableElement {

	public MItemBlock topMaterial;
	public MItemBlock midMaterial;
	public MItemBlock foundationMaterial;
	public MItemBlock seaFloorMaterial;
	public MItemBlock seaMaterial;

	public boolean spawnParticles;

	@Numeric(init = 7, min = 0, max = 256, step = 1) public int seaFloorDepth;

	public Color airColor;
	public Color fogColor;
	public Color grassColor;
	public Color foliageColor;
	public Color waterColor;
	public Color waterFogColor;

	@Numeric(init = 0.5, min = 0, max = 2.0, step = 0.1) public double temperature;
	@Numeric(init = 0.5, min = 0, max = 1.0, step = 0.1) public double downfall;
	@Numeric(init = 0, min = 0, max = 1.0, step = 0.125) public double minSnow;
	@Numeric(init = 0, min = 0, max = 1.0, step = 0.125) public double maxSnow;
	@Numeric(init = 0.5, min = 0, max = 1.0, step = 0.1) public double replacementAmount;
	@Numeric(init = 0.5, min = 0, max = 100, step = 0.1) public double replacementNoiseFrequencyScale;
	@Numeric(init = 0.1, min = 0, max = 10.0, step = 0.1) public double particleDensity;

	@LimitedOptions({ "default", "default_mutated", "stone_beach", "deep_ocean", "lowlands", "river", "ocean",
			"taiga", "mountains", "highlands", "mushroom", "less_extreme", "extreme", "beach", "swamp" })
	public String noiseType;
	@LimitedOptions({ "ash", "blue_spores", "red_spores", "white_ash" })
	public String particleToSpawn;

	public List<BiomeTagEntry> biomeTags;
	@ModElementReference public List<BiomeEntry> biomeReplacements;

	public Sound ambientSound;
	public Sound additionsSound;
	public Sound music;
	public Sound moodSound;

	private BEBiome() {
		this(null);
	}

	public BEBiome(ModElement element) {
		super(element);
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
