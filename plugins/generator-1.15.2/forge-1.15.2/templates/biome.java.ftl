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

import net.minecraft.block.material.Material;

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
			this.addStructure(Feature.STRONGHOLD.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
			</#if>

			<#if data.spawnMineshaft>
			this.addStructure(Feature.MINESHAFT.withConfiguration(new MineshaftConfig(0.004D, MineshaftStructure.Type.NORMAL)));
			</#if>

			<#if data.spawnPillagerOutpost>
			this.addStructure(Feature.PILLAGER_OUTPOST.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
			</#if>

			<#if data.villageType != "none">
			this.addStructure(Feature.VILLAGE.withConfiguration(new VillageConfig("village/${data.villageType}/town_centers", 6)));
			</#if>

			<#if data.spawnWoodlandMansion>
			this.addStructure(Feature.WOODLAND_MANSION.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
			</#if>

			<#if data.spawnJungleTemple>
			this.addStructure(Feature.JUNGLE_TEMPLE.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
			</#if>

			<#if data.spawnDesertPyramid>
			this.addStructure(Feature.DESERT_PYRAMID.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
			</#if>

			<#if data.spawnIgloo>
			this.addStructure(Feature.IGLOO.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
			</#if>

			<#if data.spawnOceanMonument>
			this.addStructure(Feature.OCEAN_MONUMENT.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
			</#if>

			<#if data.spawnShipwreck>
			this.addStructure(Feature.SHIPWRECK.withConfiguration(new ShipwreckConfig(false)));
			</#if>

			<#if data.oceanRuinType != "NONE">
			this.addStructure(Feature.OCEAN_RUIN.withConfiguration(new OceanRuinConfig(OceanRuinStructure.Type.${data.oceanRuinType}, 0.3F, 0.9F)));
			</#if>

			<#if (data.flowersPerChunk > 0)>
			addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.FLOWER.withConfiguration(DefaultBiomeFeatures.DEFAULT_FLOWER_CONFIG).withPlacement(Placement.COUNT_HEIGHTMAP_32.configure(new FrequencyConfig(${data.flowersPerChunk}))));
			</#if>

			<#if (data.grassPerChunk > 0)>
			addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.withConfiguration(DefaultBiomeFeatures.GRASS_CONFIG).withPlacement(Placement.COUNT_HEIGHTMAP_DOUBLE.configure(new FrequencyConfig(${data.grassPerChunk}))));
			</#if>

			<#if (data.seagrassPerChunk > 0)>
			this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.SEAGRASS.withConfiguration(new SeaGrassConfig(${data.seagrassPerChunk}, 0.3D)).withPlacement(Placement.TOP_SOLID_HEIGHTMAP.configure(IPlacementConfig.NO_PLACEMENT_CONFIG)));
            </#if>

			<#if (data.mushroomsPerChunk > 0)>
      		addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.withConfiguration(DefaultBiomeFeatures.BROWN_MUSHROOM_CONFIG).withPlacement(Placement.CHANCE_HEIGHTMAP_DOUBLE.configure(new ChanceConfig(${data.mushroomsPerChunk}))));
      		addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.withConfiguration(DefaultBiomeFeatures.RED_MUSHROOM_CONFIG).withPlacement(Placement.CHANCE_HEIGHTMAP_DOUBLE.configure(new ChanceConfig(${data.mushroomsPerChunk}))));
			</#if>

			<#list data.spawnTrees as spawnTree>
			<#assign tree = generator.map(spawnTree.tree.getUnmappedValue(), "trees", 1)>
			<#if tree.toString().equals("OAK_TREE")>
			addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.withConfiguration(new MultipleRandomFeatureConfig(ImmutableList.of(Feature.NORMAL_TREE.withConfiguration(DefaultBiomeFeatures.field_230129_h_).withChance(0.2F), Feature.FANCY_TREE.withConfiguration(DefaultBiomeFeatures.field_230131_m_).withChance(0.1F)), Feature.NORMAL_TREE.withConfiguration(DefaultBiomeFeatures.field_230132_o_))).withPlacement(Placement.COUNT_EXTRA_HEIGHTMAP.configure(new AtSurfaceWithExtraConfig(${spawnTree.count}, (float) ${spawnTree.extraChance}f, ${spawnTree.extraCount}))));
			<#elseif tree.toString().equals("BIG_TREE")>
			addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.withConfiguration(new MultipleRandomFeatureConfig(ImmutableList.of(Feature.FANCY_TREE.withConfiguration(DefaultBiomeFeatures.FANCY_TREE_CONFIG).withChance(0.1F)), Feature.NORMAL_TREE.withConfiguration(DefaultBiomeFeatures.JUNGLE_TREE_CONFIG))).withPlacement(Placement.COUNT_EXTRA_HEIGHTMAP.configure(new AtSurfaceWithExtraConfig(${spawnTree.count},(float)  ${spawnTree.extraChance}f, ${spawnTree.extraCount}))));
			<#elseif tree.toString().equals("SAVANNA_TREE")>
			addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.withConfiguration(new MultipleRandomFeatureConfig(ImmutableList.of(Feature.ACACIA_TREE.withConfiguration(DefaultBiomeFeatures.ACACIA_TREE_CONFIG).withChance(0.8F)), Feature.NORMAL_TREE.withConfiguration(DefaultBiomeFeatures.OAK_TREE_CONFIG))).withPlacement(Placement.COUNT_EXTRA_HEIGHTMAP.configure(new AtSurfaceWithExtraConfig(${spawnTree.count}, (float) ${spawnTree.extraChance}f, ${spawnTree.extraCount}))));
			<#elseif tree.toString().equals("MEGA_PINE_TREE")>
			addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.withConfiguration(new MultipleRandomFeatureConfig(ImmutableList.of(Feature.MEGA_SPRUCE_TREE.withConfiguration(DefaultBiomeFeatures.MEGA_PINE_TREE_CONFIG).withChance(0.30769232F)), Feature.NORMAL_TREE.withConfiguration(DefaultBiomeFeatures.SPRUCE_TREE_CONFIG))).withPlacement(Placement.COUNT_EXTRA_HEIGHTMAP.configure(new AtSurfaceWithExtraConfig(${spawnTree.count}, (float) ${spawnTree.extraChance}f, ${spawnTree.extraCount}))));
			<#elseif tree.toString().equals("MEGA_SPRUCE_TREE")>
			addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.withConfiguration(new MultipleRandomFeatureConfig(ImmutableList.of(Feature.MEGA_SPRUCE_TREE.withConfiguration(DefaultBiomeFeatures.MEGA_SPRUCE_TREE_CONFIG).withChance(0.33333334F)), Feature.NORMAL_TREE.withConfiguration(DefaultBiomeFeatures.SPRUCE_TREE_CONFIG))).withPlacement(Placement.COUNT_EXTRA_HEIGHTMAP.configure(new AtSurfaceWithExtraConfig(${spawnTree.count},(float)  ${spawnTree.extraChance}f, ${spawnTree.extraCount}))));
			<#elseif tree.toString().equals("BIRCH_TREE")>
			addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.NORMAL_TREE.withConfiguration(DefaultBiomeFeatures.field_230129_h_).withPlacement(Placement.COUNT_EXTRA_HEIGHTMAP.configure(new AtSurfaceWithExtraConfig(${spawnTree.count},(float)  ${spawnTree.extraChance}f, ${spawnTree.extraCount}))));
			<#else>
			this.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.NORMAL_TREE.withConfiguration(${tree}.CustomTree.${tree?upper_case}_CONFIG).withPlacement(Placement.COUNT_EXTRA_HEIGHTMAP.configure(new AtSurfaceWithExtraConfig(${spawnTree.count},(float) (float)  ${spawnTree.extraChance}f, ${spawnTree.extraCount}))));
			</#if>
			</#list>

			<#if (data.bigMushroomsChunk > 0)>
			addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_BOOLEAN_SELECTOR.withConfiguration(new TwoFeatureChoiceConfig(Feature.HUGE_RED_MUSHROOM.withConfiguration(DefaultBiomeFeatures.BIG_RED_MUSHROOM), Feature.HUGE_BROWN_MUSHROOM.withConfiguration(DefaultBiomeFeatures.BIG_BROWN_MUSHROOM))).withPlacement(Placement.COUNT_HEIGHTMAP.configure(new FrequencyConfig(${data.bigMushroomsChunk}))));
			</#if>

			<#if (data.reedsPerChunk > 0)>
			addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.withConfiguration(DefaultBiomeFeatures.SUGAR_CANE_CONFIG).withPlacement(Placement.COUNT_HEIGHTMAP_DOUBLE.configure(new FrequencyConfig(${data.reedsPerChunk}))));
			</#if>

			<#if (data.cactiPerChunk > 0)>
			addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.withConfiguration(DefaultBiomeFeatures.CACTUS_CONFIG).withPlacement(Placement.COUNT_HEIGHTMAP_DOUBLE.configure(new FrequencyConfig(${data.cactiPerChunk}))));
			</#if>

			<#if (data.sandPathcesPerChunk > 0)>
			addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.DISK.withConfiguration(new SphereReplaceConfig(Blocks.SAND.getDefaultState(), 7, 2, Lists.newArrayList(${mappedBlockToBlockStateCode(data.groundBlock)}, ${mappedBlockToBlockStateCode(data.undergroundBlock)}))).withPlacement(Placement.COUNT_TOP_SOLID.configure(new FrequencyConfig(${data.sandPathcesPerChunk}))));
			</#if>

			<#if (data.gravelPatchesPerChunk > 0)>
			addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.DISK.withConfiguration(new SphereReplaceConfig(Blocks.GRAVEL.getDefaultState(), 6, 2, Lists.newArrayList(${mappedBlockToBlockStateCode(data.groundBlock)}, ${mappedBlockToBlockStateCode(data.undergroundBlock)}))).withPlacement(Placement.COUNT_TOP_SOLID.configure(new FrequencyConfig(${data.gravelPatchesPerChunk}))));
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
		@OnlyIn(Dist.CLIENT) @Override public int getGrassColor(double posX, double posZ) {
			return ${data.grassColor.getRGB()};
		}
		</#if>

		<#if data.foliageColor?has_content>
		@OnlyIn(Dist.CLIENT) @Override public int getFoliageColor() {
			return ${data.foliageColor.getRGB()};
		}
		</#if>

		<#if data.airColor?has_content>
		@OnlyIn(Dist.CLIENT) @Override public int getSkyColor() {
			return ${data.airColor.getRGB()};
		}
        </#if>

	}

}
<#-- @formatter:on -->
