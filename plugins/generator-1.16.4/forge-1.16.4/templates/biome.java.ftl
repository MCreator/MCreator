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

import net.minecraft.block.material.Material;import java.util.ArrayList;import java.util.HashMap;

@${JavaModName}Elements.ModElement.Tag public class ${name}Biome extends ${JavaModName}Elements.ModElement{

	public static Biome biome;

	public ${name}Biome(${JavaModName}Elements instance) {
		super(instance, ${data.getModElement().getSortID()});
		FMLJavaModLoadingContext.get().getModEventBus().register(new BiomeRegisterHandler());
	}

	private static class BiomeRegisterHandler {

		@SubscribeEvent public void registerBiomes(RegistryEvent.Register<Biome> event) {
			if (biome == null) {
				BiomeAmbience effects = new BiomeAmbience.Builder()
						.setFogColor(${data.airColor?has_content?then(data.airColor.getRGB(), 12638463)})
						.setWaterColor(${data.waterColor?has_content?then(data.waterColor.getRGB(), 4159204)})
						.setWaterFogColor(${data.waterFogColor?has_content?then(data.waterFogColor.getRGB(), 329011)})
						.withSkyColor(${data.airColor?has_content?then(data.airColor.getRGB(), 7972607)})
						.withFoliageColor(${data.foliageColor?has_content?then(data.foliageColor.getRGB(), 10387789)})
						.withGrassColor(${data.grassColor?has_content?then(data.grassColor.getRGB(), 9470285)}).build();

				BiomeGenerationSettings.Builder biomeGenerationSettings = new BiomeGenerationSettings.Builder()
						.withSurfaceBuilder(SurfaceBuilder.DEFAULT.func_242929_a(
								new SurfaceBuilderConfig(${mappedBlockToBlockStateCode(data.groundBlock)},
									${mappedBlockToBlockStateCode(data.undergroundBlock)},
									${mappedBlockToBlockStateCode(data.undergroundBlock)})));

				<#list data.defaultFeatures as defaultFeature>
					<#assign mfeat = generator.map(defaultFeature, "defaultfeatures")>
					<#if mfeat != "null">
						DefaultBiomeFeatures.with${mfeat}(biomeGenerationSettings);
					</#if>
				</#list>

				<#if data.spawnStronghold>
				biomeGenerationSettings.withStructure(StructureFeatures.STRONGHOLD);
				</#if>

				<#if data.spawnMineshaft>
				biomeGenerationSettings.withStructure(StructureFeatures.MINESHAFT);
				</#if>

				<#if data.spawnPillagerOutpost>
				biomeGenerationSettings.withStructure(StructureFeatures.PILLAGER_OUTPOST);
				</#if>

				<#if data.villageType != "none">
				biomeGenerationSettings.withStructure(StructureFeatures.VILLAGE_${data.villageType?upper_case});
				</#if>

				<#if data.spawnWoodlandMansion>
				biomeGenerationSettings.withStructure(StructureFeatures.MANSION);
				</#if>

				<#if data.spawnJungleTemple>
				biomeGenerationSettings.withStructure(StructureFeatures.JUNGLE_PYRAMID);
				</#if>

				<#if data.spawnDesertPyramid>
				biomeGenerationSettings.withStructure(StructureFeatures.DESERT_PYRAMID);
				</#if>

				<#if data.spawnIgloo>
				biomeGenerationSettings.withStructure(StructureFeatures.IGLOO);
				</#if>

				<#if data.spawnOceanMonument>
				biomeGenerationSettings.withStructure(StructureFeatures.MONUMENT);
				</#if>

				<#if data.spawnShipwreck>
				biomeGenerationSettings.withStructure(StructureFeatures.SHIPWRECK);
				</#if>

				<#if data.oceanRuinType != "NONE">
				biomeGenerationSettings.withStructure(StructureFeatures.OCEAN_RUIN_${data.oceanRuinType});
				</#if>

				<#if (data.treesPerChunk > 0)>
					<#if data.treeType == data.TREES_CUSTOM>
					// TODO: custom tree
					</#if>

					<#if data.vanillaTreeType == "Big trees">
					biomeGenerationSettings.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
							Feature.TREE.withConfiguration((new BaseTreeFeatureConfig.Builder(
									new SimpleBlockStateProvider(Blocks.JUNGLE_LOG.getDefaultState()),
									new SimpleBlockStateProvider(Blocks.JUNGLE_LEAVES.getDefaultState()),
									new JungleFoliagePlacer(FeatureSpread.func_242252_a(2), FeatureSpread.func_242252_a(0), 2),
									new MegaJungleTrunkPlacer(10, 2, 19),
									new TwoLayerFeature(1, 1, 2))
							).setDecorators(ImmutableList.of(TrunkVineTreeDecorator.field_236879_b_, LeaveVineTreeDecorator.field_236871_b_)).build())
							.withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT)
							.withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(${data.treesPerChunk}, 0.1F, 1)))
					);
					<#elseif data.vanillaTreeType == "Savanna trees">
					biomeGenerationSettings.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
							Feature.TREE.withConfiguration((new BaseTreeFeatureConfig.Builder(
									new SimpleBlockStateProvider(Blocks.ACACIA_LOG.getDefaultState()),
									new SimpleBlockStateProvider(Blocks.ACACIA_LEAVES.getDefaultState()),
									new AcaciaFoliagePlacer(FeatureSpread.func_242252_a(2), FeatureSpread.func_242252_a(0)), new ForkyTrunkPlacer(5, 2, 2), new TwoLayerFeature(1, 0, 2))
							).setIgnoreVines().build())
							.withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT)
							.withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(${data.treesPerChunk}, 0.1F, 1)))
					);
					<#elseif data.vanillaTreeType == "Mega pine trees">
					biomeGenerationSettings.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
							Feature.TREE.withConfiguration((new BaseTreeFeatureConfig.Builder(
									new SimpleBlockStateProvider(Blocks.SPRUCE_LOG.getDefaultState()),
									new SimpleBlockStateProvider(Blocks.SPRUCE_LEAVES.getDefaultState()),
									new MegaPineFoliagePlacer(FeatureSpread.func_242252_a(0), FeatureSpread.func_242252_a(0), FeatureSpread.func_242253_a(3, 4)),
									new GiantTrunkPlacer(13, 2, 14),
									new TwoLayerFeature(1, 1, 2))
							).build())
							.withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT)
							.withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(${data.treesPerChunk}, 0.1F, 1)))
					);
					<#elseif data.vanillaTreeType == "Mega spruce trees">
					biomeGenerationSettings.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
							Feature.TREE.withConfiguration((new BaseTreeFeatureConfig.Builder(
									new SimpleBlockStateProvider(Blocks.SPRUCE_LOG.getDefaultState()),
									new SimpleBlockStateProvider(Blocks.SPRUCE_LEAVES.getDefaultState()),
									new MegaPineFoliagePlacer(FeatureSpread.func_242252_a(0), FeatureSpread.func_242252_a(0), FeatureSpread.func_242253_a(13, 4)),
									new GiantTrunkPlacer(13, 2, 14),
									new TwoLayerFeature(1, 1, 2))
							).build())
							.withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT)
							.withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(${data.treesPerChunk}, 0.1F, 1)))
					);
					<#elseif data.vanillaTreeType == "Birch trees">
					biomeGenerationSettings.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
							Feature.TREE.withConfiguration((new BaseTreeFeatureConfig.Builder(
									new SimpleBlockStateProvider(Blocks.BIRCH_LOG.getDefaultState()),
									new SimpleBlockStateProvider(Blocks.BIRCH_LEAVES.getDefaultState()),
									new BlobFoliagePlacer(FeatureSpread.func_242252_a(2), FeatureSpread.func_242252_a(0), 3), new StraightTrunkPlacer(5, 2, 0), new TwoLayerFeature(1, 0, 1))
							).setIgnoreVines().build())
							.withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT)
							.withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(${data.treesPerChunk}, 0.1F, 1)))
					);
					<#else>
					biomeGenerationSettings.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
							Feature.TREE.withConfiguration((new BaseTreeFeatureConfig.Builder(
								new SimpleBlockStateProvider(Blocks.OAK_LOG.getDefaultState()),
								new SimpleBlockStateProvider(Blocks.OAK_LEAVES.getDefaultState()),
								new BlobFoliagePlacer(FeatureSpread.func_242252_a(2), FeatureSpread.func_242252_a(0), 3),
								new StraightTrunkPlacer(4, 2, 0), new TwoLayerFeature(1, 0, 1))
							).setIgnoreVines().build())
							.withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT)
							.withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(${data.treesPerChunk}, 0.1F, 1)))
					);
					</#if>
				</#if>

				<#if (data.grassPerChunk > 0)>
				biomeGenerationSettings.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
						Feature.RANDOM_PATCH.withConfiguration(Features.Configs.GRASS_PATCH_CONFIG)
						.withPlacement(Features.Placements.PATCH_PLACEMENT)
						.withPlacement(Placement.COUNT_NOISE.configure(new NoiseDependant(-0.8D, 5, ${data.grassPerChunk}))));
				</#if>

				MobSpawnInfo.Builder mobSpawnInfo = new MobSpawnInfo.Builder().isValidSpawnBiomeForPlayer();
				<#list data.spawnEntries as spawnEntry>
					<#assign entity = generator.map(spawnEntry.entity.getUnmappedValue(), "entities", 1)!"null">
					<#if entity != "null">
						<#if !entity.toString().contains(".CustomEntity")>
						mobSpawnInfo.withSpawner(${generator.map(spawnEntry.spawnType, "mobspawntypes")}, new MobSpawnInfo.Spawners(EntityType.${entity}, ${spawnEntry.weight}, ${spawnEntry.minGroup}, ${spawnEntry.maxGroup}));
						<#else>
						mobSpawnInfo.withSpawner(${generator.map(spawnEntry.spawnType, "mobspawntypes")}, new MobSpawnInfo.Spawners(${entity.toString().replace(".CustomEntity", "")}.entity, ${spawnEntry.weight}, ${spawnEntry.minGroup}, ${spawnEntry.maxGroup}));
						</#if>
					</#if>
				</#list>

				biome = new Biome.Builder()
						.precipitation(Biome.RainType.<#if (data.rainingPossibility > 0)><#if (data.temperature > 0.15)>RAIN<#else>SNOW</#if><#else>NONE</#if>)
						.category(Biome.Category.${data.biomeCategory})
						.depth(${data.baseHeight}f)
						.scale(${data.heightVariation}f)
						.temperature(${data.temperature}f)
						.downfall(${data.rainingPossibility}f)
						.setEffects(effects)
						.withMobSpawnSettings(mobSpawnInfo.copy())
						.withGenerationSettings(biomeGenerationSettings.build())
						.build();

				event.getRegistry().register(biome.setRegistryName("${modid}:${registryname}"));
			}
		}

	}

	@Override public void init(FMLCommonSetupEvent event) {
		<#if data.biomeDictionaryTypes?has_content>
			BiomeDictionary.addTypes(RegistryKey.getOrCreateKey(Registry.BIOME_KEY, WorldGenRegistries.BIOME.getKey(biome)),
			<#list data.biomeDictionaryTypes as biomeDictionaryType>
				BiomeDictionary.Type.${generator.map(biomeDictionaryType, "biomedictionarytypes")}<#if biomeDictionaryType?has_next>,</#if>
			</#list>
			);
		</#if>
		<#if data.spawnBiome>
		BiomeManager.addBiome(BiomeManager.BiomeType.${data.biomeType},
				new BiomeManager.BiomeEntry(RegistryKey.getOrCreateKey(Registry.BIOME_KEY, WorldGenRegistries.BIOME.getKey(biome)), ${data.biomeWeight}));
        </#if>
	}

}
<#-- @formatter:on -->
