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
import net.mcreator.element.parts.Particle;
import net.mcreator.element.parts.*;
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

	public boolean hasFruits() {
		return !treeFruits.isEmpty();
	}

	public boolean hasVines() {
		return !treeVines.isEmpty();
	}

	public boolean hasStructure(String structureType) {
		return switch (structureType.toLowerCase(Locale.ENGLISH)) {
			case "mineshaft": yield spawnMineshaft;
			case "igloo": yield spawnIgloo;
			case "stronghold": yield spawnStronghold;
			case "mineshaft_mesa": yield spawnMineshaftMesa;
			case "pillager_outpost": yield spawnPillagerOutpost;
			case "woodland_mansion": yield spawnWoodlandMansion;
			case "jungle_temple": yield spawnJungleTemple;
			case "desert_pyramid": yield spawnDesertPyramid;
			case "swamp_hut": yield spawnSwampHut;
			case "ocean_monument": yield spawnOceanMonument;
			case "shipwreck": yield spawnShipwreck;
			case "shipwreck_beached": yield spawnShipwreckBeached;
			case "buried_treasure": yield spawnBuriedTreasure;
			case "nether_fortress": yield spawnNetherBridge;
			case "nether_fossil": yield spawnNetherFossil;
			case "bastion_remnant": yield spawnBastionRemnant;
			case "end_city": yield spawnEndCity;
			case "village_desert": yield villageType.equals("desert");
			case "village_plains": yield villageType.equals("plains");
			case "village_savanna": yield villageType.equals("savanna");
			case "village_snowy": yield villageType.equals("snowy");
			case "village_taiga": yield villageType.equals("taiga");
			case "ocean_ruin_cold": yield oceanRuinType.equals("COLD");
			case "ocean_ruin_warm": yield oceanRuinType.equals("WARM");
			case "ruined_portal_standard": yield spawnRuinedPortal.equals("STANDARD");
			case "ruined_portal_desert": yield spawnRuinedPortal.equals("DESERT");
			case "ruined_portal_jungle": yield spawnRuinedPortal.equals("JUNGLE");
			case "ruined_portal_swamp": yield spawnRuinedPortal.equals("SWAMP");
			case "ruined_portal_mountain": yield spawnRuinedPortal.equals("MOUNTAIN");
			case "ruined_portal_ocean": yield spawnRuinedPortal.equals("OCEAN");
			case "ruined_portal_nether": yield spawnRuinedPortal.equals("NETHER");
			default: yield false;
		};
	}

	@Override public BufferedImage generateModElementPicture() {
		return MinecraftImageGenerator.Preview.generateBiomePreviewPicture(getModElement().getWorkspace(), airColor,
				grassColor, waterColor, groundBlock, undergroundBlock, treesPerChunk, treeType, treeStem, treeBranch);
	}

}
