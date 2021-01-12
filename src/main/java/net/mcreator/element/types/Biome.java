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
import net.mcreator.element.parts.BiomeEntry;
import net.mcreator.element.parts.EntityEntry;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.parts.Particle;
import net.mcreator.element.parts.Sound;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.workspace.elements.ModElement;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused") public class Biome extends GeneratableElement {

	public final transient int TREES_VANILLA;
	public final transient int TREES_CUSTOM;

	public String name;

	public MItemBlock groundBlock;
	public MItemBlock undergroundBlock;

	public Color airColor;
	public Color grassColor;
	public Color foliageColor;
	public Color waterColor;
	public Color waterFogColor;

	public Sound ambientSound;
	public Sound additionsSound;
	public Sound music;
	public Sound moodSound;
	public int moodSoundDelay;

	public boolean spawnParticles;
	public Particle particleToSpawn;
	public double particlesProbability;

	public String biomeCategory;
	public BiomeEntry parent;
	public double rainingPossibility;
	public double temperature;
	public double baseHeight;
	public double heightVariation;
	public String biomeType;

	public boolean spawnBiome;
	public int biomeWeight;
	public List<String> biomeDictionaryTypes;

	public int grassPerChunk;
	public int seagrassPerChunk;
	public int flowersPerChunk;
	public int mushroomsPerChunk;
	public int bigMushroomsChunk;
	public int sandPatchesPerChunk;
	public int gravelPatchesPerChunk;
	public int reedsPerChunk;
	public int cactiPerChunk;

	public int treesPerChunk;
	public String vanillaTreeType;
	public int treeType;
	public int minHeight;
	public int maxWaterDepth;
	public MItemBlock treeStem;
	public MItemBlock treeBranch;
	public MItemBlock treeVines;
	public MItemBlock treeFruits;

	public boolean spawnStronghold;
	public boolean spawnMineshaft;
	public boolean spawnPillagerOutpost;
	public String villageType;
	public boolean spawnWoodlandMansion;
	public boolean spawnJungleTemple;
	public boolean spawnDesertPyramid;
	public boolean spawnIgloo;
	public boolean spawnOceanMonument;
	public boolean spawnShipwreck;
	public String oceanRuinType;

	public List<String> defaultFeatures;

	public List<SpawnEntry> spawnEntries;

	private Biome() {
		this(null);
	}

	public Biome(ModElement element) {
		super(element);

		// RESTORE CONSTANTS (FOR GSON)
		TREES_VANILLA = 0;
		TREES_CUSTOM = 1;

		// DEFAULT VALUES
		name = "";
		spawnStronghold = true;
		spawnMineshaft = true;
		spawnPillagerOutpost = true;
		vanillaTreeType = "Default";
		villageType = "none";
		oceanRuinType = "NONE";
		biomeCategory = "NONE";
		biomeDictionaryTypes = new ArrayList<>();
		spawnEntries = new ArrayList<>();
		defaultFeatures = new ArrayList<>();
	}

	public static class SpawnEntry {

		public EntityEntry entity;
		public int minGroup;
		public int maxGroup;
		public int weight;
		public String spawnType;

	}

	@Override public BufferedImage generateModElementPicture() {
		return MinecraftImageGenerator.Preview
				.generateBiomePreviewPicture(getModElement().getWorkspace(), airColor, grassColor, waterColor,
						groundBlock, undergroundBlock, treesPerChunk, treeType, treeStem, treeBranch);
	}

}
