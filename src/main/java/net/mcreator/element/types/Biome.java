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
import java.util.Locale;

@SuppressWarnings("unused") public class Biome extends GeneratableElement {

	public final transient int TREES_VANILLA;
	public final transient int TREES_CUSTOM;

	public String name;

	public MItemBlock groundBlock;
	public MItemBlock undergroundBlock;
	public MItemBlock underwaterBlock;

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

	public double rainingPossibility;
	public double temperature;

	public boolean spawnBiome;
	public boolean spawnInCaves;
	public boolean spawnBiomeNether;

	public ClimatePoint genTemperature;
	public ClimatePoint genHumidity;
	public ClimatePoint genContinentalness;
	public ClimatePoint genErosion;
	public ClimatePoint genWeirdness;

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
	public MItemBlock treeStem;
	public MItemBlock treeBranch;
	public MItemBlock treeVines;
	public MItemBlock treeFruits;

	public boolean spawnStronghold;
	public boolean spawnMineshaft;
	public boolean spawnMineshaftMesa;
	public boolean spawnPillagerOutpost;
	public String villageType;
	public boolean spawnWoodlandMansion;
	public boolean spawnJungleTemple;
	public boolean spawnDesertPyramid;
	public boolean spawnSwampHut;
	public boolean spawnIgloo;
	public boolean spawnOceanMonument;
	public boolean spawnShipwreck;
	public boolean spawnShipwreckBeached;
	public boolean spawnBuriedTreasure;
	public String oceanRuinType;
	public boolean spawnNetherBridge;
	public boolean spawnNetherFossil;
	public boolean spawnBastionRemnant;
	public boolean spawnEndCity;
	public String spawnRuinedPortal;

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
		spawnRuinedPortal = "NONE";
		spawnEntries = new ArrayList<>();
		defaultFeatures = new ArrayList<>();
	}

	public boolean hasFruits() {
		return treeFruits != null && !treeFruits.isEmpty();
	}

	public boolean hasVines() {
		return treeVines != null && !treeVines.isEmpty();
	}

	public boolean hasStructure(String structureType) {
		return switch (structureType.toLowerCase(Locale.ENGLISH)) {
			case "mineshaft" -> spawnMineshaft;
			case "igloo" -> spawnIgloo;
			case "stronghold" -> spawnStronghold;
			case "mineshaft_mesa" -> spawnMineshaftMesa;
			case "pillager_outpost" -> spawnPillagerOutpost;
			case "woodland_mansion" -> spawnWoodlandMansion;
			case "jungle_temple" -> spawnJungleTemple;
			case "desert_pyramid" -> spawnDesertPyramid;
			case "swamp_hut" -> spawnSwampHut;
			case "ocean_monument" -> spawnOceanMonument;
			case "shipwreck" -> spawnShipwreck;
			case "shipwreck_beached" -> spawnShipwreckBeached;
			case "buried_treasure" -> spawnBuriedTreasure;
			case "nether_fortress" -> spawnNetherBridge;
			case "nether_fossil" -> spawnNetherFossil;
			case "bastion_remnant" -> spawnBastionRemnant;
			case "end_city" -> spawnEndCity;
			case "village_desert" -> villageType.equals("desert");
			case "village_plains" -> villageType.equals("plains");
			case "village_savanna" -> villageType.equals("savanna");
			case "village_snowy" -> villageType.equals("snowy");
			case "village_taiga" -> villageType.equals("taiga");
			case "ocean_ruin_cold" -> oceanRuinType.equals("COLD");
			case "ocean_ruin_warm" -> oceanRuinType.equals("WARM");
			case "ruined_portal_standard" -> spawnRuinedPortal.equals("STANDARD");
			case "ruined_portal_desert" -> spawnRuinedPortal.equals("DESERT");
			case "ruined_portal_jungle" -> spawnRuinedPortal.equals("JUNGLE");
			case "ruined_portal_swamp" -> spawnRuinedPortal.equals("SWAMP");
			case "ruined_portal_mountain" -> spawnRuinedPortal.equals("MOUNTAIN");
			case "ruined_portal_ocean" -> spawnRuinedPortal.equals("OCEAN");
			case "ruined_portal_nether" -> spawnRuinedPortal.equals("NETHER");
			default -> false;
		};
	}

	public MItemBlock getUnderwaterBlock() {
		if (underwaterBlock == null || underwaterBlock.isEmpty())
			return undergroundBlock;

		return underwaterBlock;
	}

	@Override public BufferedImage generateModElementPicture() {
		return MinecraftImageGenerator.Preview.generateBiomePreviewPicture(getModElement().getWorkspace(), airColor,
				grassColor, waterColor, groundBlock, undergroundBlock, treesPerChunk, treeType, treeStem, treeBranch);
	}

	public static class SpawnEntry {

		public EntityEntry entity;
		public int minGroup;
		public int maxGroup;
		public int weight;
		public String spawnType;

	}

	public static class ClimatePoint {

		public double min;
		public double max;

		public ClimatePoint(double min, double max) {
			this.min = min;
			this.max = max;
		}
	}

}
