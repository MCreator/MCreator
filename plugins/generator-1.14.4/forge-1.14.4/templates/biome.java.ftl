<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2020 Pylo and contributors
 #
 # This program is free software: you can redistribute it and/or modify
 # it under the terms of the GNU General Public License as published by
 # the Free Software Foundation, either version 3 of the License, or
 # (at your option) any later version.
 #
 # This program is distributed in the hope that it will be useful,
 # but WITHOUT ANY WARRANTY; without even the implied warranty of
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 # GNU General Public License for more details.
 #
 # You should have received a copy of the GNU General Public License
 # along with this program.  If not, see <https://www.gnu.org/licenses/>.
 #
 # Additional permission for code generator templates (*.ftl files)
 #
 # As a special exception, you may create a larger work that contains part or
 # all of the MCreator code generator templates (*.ftl files) and distribute
 # that work under terms of your choice, so long as that work isn't itself a
 # template for code generation. Alternatively, if you modify or redistribute
 # the template itself, you may (at your option) remove this special exception,
 # which will cause the template and the resulting code generator output files
 # to be licensed under the GNU General Public License without this special
 # exception.
-->

<#-- @formatter:off -->
<#include "mcitems.ftl">

package ${package}.world.biome;

@${JavaModName}Elements.ModElement.Tag public class ${name}Biome extends ${JavaModName}Elements.ModElement{

	@ObjectHolder("${modid}:${registryname}")
	public static final CustomBiome biome = null;

	public ${name}Biome (${JavaModName}Elements instance) {
		super(instance, ${data.getModElement().getSortID()});
	}

	@Override public void initElements() {
		elements.biomes.add(() -> new CustomBiome());
	}

	@Override public void init(FMLCommonSetupEvent event) {
		<#if data.biomeDictionaryTypes?has_content>
			BiomeDictionary.addTypes(biome,
			<#list data.biomeDictionaryTypes as biomeDictionaryType>
				BiomeDictionary.Type.${generator.map(biomeDictionaryType, "biomedictionarytypes")}<#if biomeDictionaryType?has_next>,</#if>
			</#list>
			);
		</#if>
		<#if data.spawnBiome>
		BiomeManager.addSpawnBiome(biome);
		BiomeManager.addBiome(BiomeManager.BiomeType.${data.biomeType}, new BiomeManager.BiomeEntry(biome, ${data.biomeWeight}));
        </#if>
	}

	static class CustomBiome extends Biome {

		public CustomBiome() {
			super(new Biome.Builder().downfall(${data.rainingPossibility}f)
				.depth(${data.baseHeight}f)
				.scale(${data.heightVariation}f)
				.temperature(${data.temperature}f)
				.precipitation(Biome.RainType.<#if (data.rainingPossibility > 0)><#if (data.temperature > 0.15)>RAIN<#else>SNOW</#if><#else>NONE</#if>)
				.category(Biome.Category.${data.biomeCategory})
				<#if data.waterColor?has_content>
				.waterColor(${data.waterColor.getRGB()})
				<#else>
				.waterColor(4159204)
				</#if>
				<#if data.waterFogColor?has_content>
				.waterFogColor(${data.waterFogColor.getRGB()})
				<#else>
				.waterFogColor(329011)
				</#if>
				<#if data.parent?? && data.parent.getUnmappedValue() != "No parent">
				.parent("${data.parent}")
				</#if>
				.surfaceBuilder(SurfaceBuilder.DEFAULT, new SurfaceBuilderConfig(
				${mappedBlockToBlockStateCode(data.groundBlock)}, ${mappedBlockToBlockStateCode(data.undergroundBlock)}, ${mappedBlockToBlockStateCode(data.undergroundBlock)}))
			);

			setRegistryName("${registryname}");

			<#list data.defaultFeatures as defaultFeature>
			DefaultBiomeFeatures.add${generator.map(defaultFeature, "defaultfeatures")}(this);
			</#list>

			<#if data.spawnStronghold>
			this.addStructure(Feature.STRONGHOLD, IFeatureConfig.NO_FEATURE_CONFIG);
			</#if>

			<#if data.spawnMineshaft>
			this.addStructure(Feature.MINESHAFT, new MineshaftConfig(0.004D, MineshaftStructure.Type.NORMAL));
			</#if>

			<#if data.spawnPillagerOutpost>
			this.addStructure(Feature.PILLAGER_OUTPOST, new PillagerOutpostConfig(0.004D));
			</#if>

			<#if data.villageType != "none">
			this.addStructure(Feature.VILLAGE, new VillageConfig("village/${data.villageType}/town_centers", 6));
			</#if>

			<#if data.spawnWoodlandMansion>
			this.addStructure(Feature.WOODLAND_MANSION, IFeatureConfig.NO_FEATURE_CONFIG);
			</#if>

			<#if data.spawnJungleTemple>
			this.addStructure(Feature.JUNGLE_TEMPLE, IFeatureConfig.NO_FEATURE_CONFIG);
			</#if>

			<#if data.spawnDesertPyramid>
			this.addStructure(Feature.DESERT_PYRAMID, IFeatureConfig.NO_FEATURE_CONFIG);
			</#if>

			<#if data.spawnIgloo>
			this.addStructure(Feature.IGLOO, IFeatureConfig.NO_FEATURE_CONFIG);
			</#if>

			<#if data.spawnOceanMonument>
			this.addStructure(Feature.OCEAN_MONUMENT, IFeatureConfig.NO_FEATURE_CONFIG);
			</#if>

			<#if data.spawnShipwreck>
			this.addStructure(Feature.SHIPWRECK, new ShipwreckConfig(false));
			</#if>

			<#if data.oceanRuinType != "NONE">
			this.addStructure(Feature.OCEAN_RUIN, new OceanRuinConfig(OceanRuinStructure.Type.${data.oceanRuinType}, 0.3F, 0.9F));
			</#if>

			<#if (data.flowersPerChunk > 0)>
			addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(Feature.DEFAULT_FLOWER, IFeatureConfig.NO_FEATURE_CONFIG, Placement.COUNT_HEIGHTMAP_32, new FrequencyConfig(${data.flowersPerChunk})));
			</#if>

			<#if (data.grassPerChunk > 0)>
			addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(Feature.GRASS, new GrassFeatureConfig(Blocks.GRASS.getDefaultState()), Placement.COUNT_HEIGHTMAP_DOUBLE, new FrequencyConfig(${data.grassPerChunk})));
			</#if>

			<#if (data.seagrassPerChunk > 0)>
			this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, createDecoratedFeature(Feature.SEAGRASS, new SeaGrassConfig(${data.seagrassPerChunk}, 0.3D), Placement.TOP_SOLID_HEIGHTMAP, IPlacementConfig.NO_PLACEMENT_CONFIG));
			</#if>

			<#if (data.mushroomsPerChunk > 0)>
      		addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(Feature.BUSH, new BushConfig(Blocks.BROWN_MUSHROOM.getDefaultState()), Placement.CHANCE_HEIGHTMAP_DOUBLE, new ChanceConfig(${data.mushroomsPerChunk})));
      		addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(Feature.BUSH, new BushConfig(Blocks.RED_MUSHROOM.getDefaultState()), Placement.CHANCE_HEIGHTMAP_DOUBLE, new ChanceConfig(${data.mushroomsPerChunk})));
			</#if>

			<#list data.spawnTrees as spawnTree>
			<#assign tree = generator.map(spawnTree.tree.getUnmappedValue(), "trees", 1)>
			<#if tree.toString().equals("ACACIA")>
			addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(Feature.SAVANNA_TREE, IFeatureConfig.NO_FEATURE_CONFIG, Placement.COUNT_EXTRA_HEIGHTMAP, new AtSurfaceWithExtraConfig(${spawnTree.count},(float) ${spawnTree.extraChance}f, ${spawnTree.extraCount})));
			<#elseif tree.toString().equals("BIRCH")>
			addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(Feature.BIRCH_TREE, IFeatureConfig.NO_FEATURE_CONFIG, Placement.COUNT_EXTRA_HEIGHTMAP, new AtSurfaceWithExtraConfig(${spawnTree.count},(float) ${spawnTree.extraChance}f, ${spawnTree.extraCount})));
			<#elseif tree.toString().equals("DARK_OAK")>
			addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(Feature.BIRCH_TREE, IFeatureConfig.NO_FEATURE_CONFIG, Placement.COUNT_EXTRA_HEIGHTMAP, new AtSurfaceWithExtraConfig(${spawnTree.count},(float) ${spawnTree.extraChance}f, ${spawnTree.extraCount})));
			<#elseif tree.toString().equals("FANCY")>
			addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(Feature.FANCY_TREE, IFeatureConfig.NO_FEATURE_CONFIG, Placement.COUNT_EXTRA_HEIGHTMAP, new AtSurfaceWithExtraConfig(${spawnTree.count},(float) ${spawnTree.extraChance}f, ${spawnTree.extraCount})));
			<#elseif tree.toString().equals("JUNGLE")>
			addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(Feature.JUNGLE_TREE, IFeatureConfig.NO_FEATURE_CONFIG, Placement.COUNT_EXTRA_HEIGHTMAP, new AtSurfaceWithExtraConfig(${spawnTree.count},(float) ${spawnTree.extraChance}f, ${spawnTree.extraCount})));
			<#elseif tree.toString().equals("JUNGLE_GROUND_BUSH")>
			addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(Feature.JUNGLE_GROUND_BUSH, IFeatureConfig.NO_FEATURE_CONFIG, Placement.COUNT_EXTRA_HEIGHTMAP, new AtSurfaceWithExtraConfig(${spawnTree.count},(float) ${spawnTree.extraChance}f, ${spawnTree.extraCount})));
			<#elseif tree.toString().equals("OAK")>
			addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(Feature.NORMAL_TREE, IFeatureConfig.NO_FEATURE_CONFIG, Placement.COUNT_EXTRA_HEIGHTMAP, new AtSurfaceWithExtraConfig(${spawnTree.count},(float) ${spawnTree.extraChance}f, ${spawnTree.extraCount})));
			<#elseif tree.toString().equals("MEGA_JUNGLE")>
			addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(Feature.MEGA_JUNGLE_TREE, IFeatureConfig.NO_FEATURE_CONFIG, Placement.COUNT_EXTRA_HEIGHTMAP, new AtSurfaceWithExtraConfig(${spawnTree.count},(float) ${spawnTree.extraChance}f, ${spawnTree.extraCount})));
			<#elseif tree.toString().equals("MEGA_PINE")>
			addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(Feature.MEGA_PINE_TREE, IFeatureConfig.NO_FEATURE_CONFIG, Placement.COUNT_EXTRA_HEIGHTMAP, new AtSurfaceWithExtraConfig(${spawnTree.count},(float) ${spawnTree.extraChance}f, ${spawnTree.extraCount})));
			<#elseif tree.toString().equals("MEGA_SPRUCE")>
			addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(Feature.MEGA_SPRUCE_TREE, IFeatureConfig.NO_FEATURE_CONFIG, Placement.COUNT_EXTRA_HEIGHTMAP, new AtSurfaceWithExtraConfig(${spawnTree.count},(float) ${spawnTree.extraChance}f, ${spawnTree.extraCount})));
			<#elseif tree.toString().equals("SWAMP")>
			addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(Feature.SWAMP_TREE, IFeatureConfig.NO_FEATURE_CONFIG, Placement.COUNT_EXTRA_HEIGHTMAP, new AtSurfaceWithExtraConfig(${spawnTree.count},(float) ${spawnTree.extraChance}f, ${spawnTree.extraCount})));
			</#if>
			</#list>

			<#if (data.bigMushroomsChunk > 0)>
			addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(Feature.RANDOM_BOOLEAN_SELECTOR, new TwoFeatureChoiceConfig(Feature.HUGE_RED_MUSHROOM, new BigMushroomFeatureConfig(false), Feature.HUGE_BROWN_MUSHROOM, new BigMushroomFeatureConfig(false)), Placement.COUNT_HEIGHTMAP, new FrequencyConfig(${data.bigMushroomsChunk})));
			</#if>

			<#if (data.reedsPerChunk > 0)>
			addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(Feature.REED, IFeatureConfig.NO_FEATURE_CONFIG, Placement.COUNT_HEIGHTMAP_DOUBLE, new FrequencyConfig(${data.reedsPerChunk})));
			</#if>

			<#if (data.cactiPerChunk > 0)>
			addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(Feature.CACTUS, IFeatureConfig.NO_FEATURE_CONFIG, Placement.COUNT_HEIGHTMAP_DOUBLE, new FrequencyConfig(${data.cactiPerChunk})));
			</#if>

			<#if (data.sandPathcesPerChunk > 0)>
			addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createDecoratedFeature(Feature.DISK, new SphereReplaceConfig(Blocks.SAND.getDefaultState(), 7, 2, Lists.newArrayList(${mappedBlockToBlockStateCode(data.groundBlock)}, ${mappedBlockToBlockStateCode(data.undergroundBlock)})), Placement.COUNT_TOP_SOLID, new FrequencyConfig(${data.sandPathcesPerChunk})));
			</#if>

			<#if (data.gravelPatchesPerChunk > 0)>
			addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createDecoratedFeature(Feature.DISK, new SphereReplaceConfig(Blocks.GRAVEL.getDefaultState(), 6, 2, Lists.newArrayList(${mappedBlockToBlockStateCode(data.groundBlock)}, ${mappedBlockToBlockStateCode(data.undergroundBlock)})), Placement.COUNT_TOP_SOLID, new FrequencyConfig(${data.gravelPatchesPerChunk})));
			</#if>

			<#list data.spawnEntries as spawnEntry>
				<#assign entity = generator.map(spawnEntry.entity.getUnmappedValue(), "entities", 1)!"null">
				<#if entity != "null">
					<#if !entity.toString().contains(".CustomEntity")>
						this.addSpawn(${generator.map(spawnEntry.spawnType, "mobspawntypes")}, new Biome.SpawnListEntry(EntityType.${entity}, ${spawnEntry.weight}, ${spawnEntry.minGroup}, ${spawnEntry.maxGroup}));
					<#else>
						this.addSpawn(${generator.map(spawnEntry.spawnType, "mobspawntypes")}, new Biome.SpawnListEntry(${entity.toString().replace(".CustomEntity", "")}.entity, ${spawnEntry.weight}, ${spawnEntry.minGroup}, ${spawnEntry.maxGroup}));
					</#if>
				</#if>
			</#list>
		}

		<#if data.grassColor?has_content>
		@OnlyIn(Dist.CLIENT) @Override public int getGrassColor(BlockPos pos) {
			return ${data.grassColor.getRGB()};
		}
        </#if>

		<#if data.foliageColor?has_content>
        @OnlyIn(Dist.CLIENT) @Override public int getFoliageColor(BlockPos pos) {
        	return ${data.foliageColor.getRGB()};
        }
        </#if>

		<#if data.airColor?has_content>
		@OnlyIn(Dist.CLIENT) @Override public int getSkyColorByTemp(float currentTemperature) {
			return ${data.airColor.getRGB()};
		}
    </#if>
	}
}
<#-- @formatter:on -->
