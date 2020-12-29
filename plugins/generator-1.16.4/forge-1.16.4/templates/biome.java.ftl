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
						.withGrassColor(${data.grassColor?has_content?then(data.grassColor.getRGB(), 9470285)})
						<#if data.ambientSound.getMappedValue()?has_content>
						.setAmbientSound((net.minecraft.util.SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("${data.ambientSound}")))
                        </#if>
						<#if data.moodSound.getMappedValue()?has_content>
                        .setMoodSound(new MoodSoundAmbience((net.minecraft.util.SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("${data.moodSound}")), ${data.moodSoundDelay}, 8, 2.0D))
                        </#if>
						<#if data.additionsSound.getMappedValue()?has_content>
                        .setAdditionsSound(new SoundAdditionsAmbience((net.minecraft.util.SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("${data.additionsSound}")), 0.0111D))
                        </#if>
						<#if data.music.getMappedValue()?has_content>
                        .setMusic(BackgroundMusicTracks.getDefaultBackgroundMusicSelector((net.minecraft.util.SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("${data.music}"))))
                        </#if>
                        <#if data.spawnParticles>
                        .setParticle(new ParticleEffectAmbience(${data.particleToSpawn}, ${data.particlesProbability / 100}f))
                        </#if>
                        .build();

				BiomeGenerationSettings.Builder biomeGenerationSettings = new BiomeGenerationSettings.Builder()
						.withSurfaceBuilder(SurfaceBuilder.DEFAULT.func_242929_a(
								new SurfaceBuilderConfig(${mappedBlockToBlockStateCode(data.groundBlock)},
									${mappedBlockToBlockStateCode(data.undergroundBlock)},
									${mappedBlockToBlockStateCode(data.undergroundBlock)})));

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
					<#assign ct = data.treeType == data.TREES_CUSTOM>
					<#if ct>
					</#if>

					<#if data.vanillaTreeType == "Big trees">
					biomeGenerationSettings.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
							Feature.TREE.withConfiguration((new BaseTreeFeatureConfig.Builder(
								new SimpleBlockStateProvider(${ct?then(mappedBlockToBlockStateCode(data.treeStem), "Blocks.JUNGLE_LOG.getDefaultState()")}),
								new SimpleBlockStateProvider(${ct?then(mappedBlockToBlockStateCode(data.treeBranch), "Blocks.JUNGLE_LEAVES.getDefaultState()")}),
								new JungleFoliagePlacer(FeatureSpread.func_242252_a(2), FeatureSpread.func_242252_a(0), 2),
								new MegaJungleTrunkPlacer(${ct?then(data.minHeight, 10)}, 2, 19),
								new TwoLayerFeature(1, 1, 2)))
								<#if (data.treeVines?has_content && !data.treeVines.isEmpty()) || (data.treeFruits?has_content && !data.treeFruits.isEmpty())>
									<@vinesAndCocoa/>
								<#else>
									.setDecorators(ImmutableList.of(TrunkVineTreeDecorator.field_236879_b_, LeaveVineTreeDecorator.field_236871_b_))
								</#if>
							.build())
							.withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT)
							.withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(${data.treesPerChunk}, 0.1F, 1)))
					);
					<#elseif data.vanillaTreeType == "Savanna trees">
					biomeGenerationSettings.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
							Feature.TREE.withConfiguration((new BaseTreeFeatureConfig.Builder(
								new SimpleBlockStateProvider(${ct?then(mappedBlockToBlockStateCode(data.treeStem), "Blocks.ACACIA_LOG.getDefaultState()")}),
								new SimpleBlockStateProvider(${ct?then(mappedBlockToBlockStateCode(data.treeBranch), "Blocks.ACACIA_LEAVES.getDefaultState()")}),
								new AcaciaFoliagePlacer(FeatureSpread.func_242252_a(2), FeatureSpread.func_242252_a(0)),
								new ForkyTrunkPlacer(${ct?then(data.minHeight, 5)}, 2, 2),
								new TwoLayerFeature(1, 0, 2)))
								<#if (data.treeVines?has_content && !data.treeVines.isEmpty()) || (data.treeFruits?has_content && !data.treeFruits.isEmpty())>
									<@vinesAndCocoa/>
								<#else>
									.setIgnoreVines()
								</#if>
							.build())
							.withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT)
							.withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(${data.treesPerChunk}, 0.1F, 1)))
					);
					<#elseif data.vanillaTreeType == "Mega pine trees">
					biomeGenerationSettings.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
							Feature.TREE.withConfiguration((new BaseTreeFeatureConfig.Builder(
								new SimpleBlockStateProvider(${ct?then(mappedBlockToBlockStateCode(data.treeStem), "Blocks.SPRUCE_LOG.getDefaultState()")}),
								new SimpleBlockStateProvider(${ct?then(mappedBlockToBlockStateCode(data.treeBranch), "Blocks.SPRUCE_LEAVES.getDefaultState()")}),
								new MegaPineFoliagePlacer(FeatureSpread.func_242252_a(0), FeatureSpread.func_242252_a(0), FeatureSpread.func_242253_a(3, 4)),
								new GiantTrunkPlacer(${ct?then(data.minHeight, 13)}, 2, 14),
								new TwoLayerFeature(1, 1, 2)))
								<#if (data.treeVines?has_content && !data.treeVines.isEmpty()) || (data.treeFruits?has_content && !data.treeFruits.isEmpty())>
									<@vinesAndCocoa/>
								</#if>
							.build())
							.withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT)
							.withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(${data.treesPerChunk}, 0.1F, 1)))
					);
					<#elseif data.vanillaTreeType == "Mega spruce trees">
					biomeGenerationSettings.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
							Feature.TREE.withConfiguration((new BaseTreeFeatureConfig.Builder(
								new SimpleBlockStateProvider(${ct?then(mappedBlockToBlockStateCode(data.treeStem), "Blocks.SPRUCE_LOG.getDefaultState()")}),
								new SimpleBlockStateProvider(${ct?then(mappedBlockToBlockStateCode(data.treeBranch), "Blocks.SPRUCE_LEAVES.getDefaultState()")}),
								new MegaPineFoliagePlacer(FeatureSpread.func_242252_a(0), FeatureSpread.func_242252_a(0), FeatureSpread.func_242253_a(13, 4)),
								new GiantTrunkPlacer(${ct?then(data.minHeight, 13)}, 2, 14),
								new TwoLayerFeature(1, 1, 2)))
								<#if (data.treeVines?has_content && !data.treeVines.isEmpty()) || (data.treeFruits?has_content && !data.treeFruits.isEmpty())>
									<@vinesAndCocoa/>
								</#if>
							.build())
							.withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT)
							.withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(${data.treesPerChunk}, 0.1F, 1)))
					);
					<#elseif data.vanillaTreeType == "Birch trees">
					biomeGenerationSettings.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
							Feature.TREE.withConfiguration((new BaseTreeFeatureConfig.Builder(
								new SimpleBlockStateProvider(${ct?then(mappedBlockToBlockStateCode(data.treeStem), "Blocks.BIRCH_LOG.getDefaultState()")}),
								new SimpleBlockStateProvider(${ct?then(mappedBlockToBlockStateCode(data.treeBranch), "Blocks.BIRCH_LEAVES.getDefaultState()")}),
								new BlobFoliagePlacer(FeatureSpread.func_242252_a(2), FeatureSpread.func_242252_a(0), 3),
								new StraightTrunkPlacer(${ct?then(data.minHeight, 5)}, 2, 0),
								new TwoLayerFeature(1, 0, 1)))
								<#if (data.treeVines?has_content && !data.treeVines.isEmpty()) || (data.treeFruits?has_content && !data.treeFruits.isEmpty())>
									<@vinesAndCocoa/>
								<#else>
									.setIgnoreVines()
								</#if>
							.build())
							.withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT)
							.withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(${data.treesPerChunk}, 0.1F, 1)))
					);
					<#else>
					biomeGenerationSettings.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
							Feature.TREE.withConfiguration((new BaseTreeFeatureConfig.Builder(
								new SimpleBlockStateProvider(${ct?then(mappedBlockToBlockStateCode(data.treeStem), "Blocks.OAK_LOG.getDefaultState()")}),
								new SimpleBlockStateProvider(${ct?then(mappedBlockToBlockStateCode(data.treeBranch), "Blocks.OAK_LEAVES.getDefaultState()")}),
								new BlobFoliagePlacer(FeatureSpread.func_242252_a(2), FeatureSpread.func_242252_a(0), 3),
								new StraightTrunkPlacer(${ct?then(data.minHeight, 4)}, 2, 0),
								new TwoLayerFeature(1, 0, 1)))
								<#if (data.treeVines?has_content && !data.treeVines.isEmpty()) || (data.treeFruits?has_content && !data.treeFruits.isEmpty())>
									<@vinesAndCocoa/>
								<#else>
									.setIgnoreVines()
								</#if>
							.build())
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

				<#if (data.seagrassPerChunk > 0)>
				biomeGenerationSettings.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
						Feature.SEAGRASS.withConfiguration(new ProbabilityConfig(0.3F))
								.func_242731_b(${data.seagrassPerChunk})
								.withPlacement(Features.Placements.SEAGRASS_DISK_PLACEMENT));
				</#if>

				<#if (data.flowersPerChunk > 0)>
				biomeGenerationSettings.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
						Feature.FLOWER.withConfiguration(Features.Configs.NORMAL_FLOWER_CONFIG)
								.withPlacement(Features.Placements.VEGETATION_PLACEMENT)
								.withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT)
								.func_242731_b(${data.flowersPerChunk}));
				</#if>

				<#if (data.mushroomsPerChunk > 0)>
				biomeGenerationSettings.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
						Feature.RANDOM_PATCH.withConfiguration((new BlockClusterFeatureConfig.Builder(
								new SimpleBlockStateProvider(Blocks.BROWN_MUSHROOM.getDefaultState()), SimpleBlockPlacer.PLACER))
								.tries(${data.mushroomsPerChunk}).func_227317_b_().build()));
				biomeGenerationSettings.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
						Feature.RANDOM_PATCH.withConfiguration((new BlockClusterFeatureConfig.Builder(
								new SimpleBlockStateProvider(Blocks.RED_MUSHROOM.getDefaultState()), SimpleBlockPlacer.PLACER))
								.tries(${data.mushroomsPerChunk}).func_227317_b_().build()));
				</#if>

				<#if (data.bigMushroomsChunk > 0)>
				biomeGenerationSettings.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
						Feature.HUGE_BROWN_MUSHROOM.withConfiguration(new BigMushroomFeatureConfig(
								new SimpleBlockStateProvider(Blocks.BROWN_MUSHROOM_BLOCK.getDefaultState().with(HugeMushroomBlock.UP, Boolean.TRUE).with(HugeMushroomBlock.DOWN, Boolean.FALSE)),
								new SimpleBlockStateProvider(Blocks.MUSHROOM_STEM.getDefaultState().with(HugeMushroomBlock.UP, Boolean.FALSE).with(HugeMushroomBlock.DOWN, Boolean.FALSE)), ${data.bigMushroomsChunk})));
				biomeGenerationSettings.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
						Feature.HUGE_RED_MUSHROOM.withConfiguration(new BigMushroomFeatureConfig(
								new SimpleBlockStateProvider(Blocks.RED_MUSHROOM_BLOCK.getDefaultState().with(HugeMushroomBlock.DOWN, Boolean.FALSE)),
								new SimpleBlockStateProvider(Blocks.MUSHROOM_STEM.getDefaultState().with(HugeMushroomBlock.UP, Boolean.FALSE).with(HugeMushroomBlock.DOWN, Boolean.FALSE)), ${data.bigMushroomsChunk})));
				</#if>

				<#if (data.sandPatchesPerChunk > 0)>
				biomeGenerationSettings.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
						Feature.DISK.withConfiguration(new SphereReplaceConfig(Blocks.SAND.getDefaultState(), FeatureSpread.func_242253_a(2, 4), 2,
								ImmutableList.of(${mappedBlockToBlockStateCode(data.groundBlock)}, ${mappedBlockToBlockStateCode(data.undergroundBlock)})))
								.withPlacement(Features.Placements.SEAGRASS_DISK_PLACEMENT).func_242731_b(${data.sandPatchesPerChunk}));
				</#if>

				<#if (data.gravelPatchesPerChunk > 0)>
				biomeGenerationSettings.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
						Feature.DISK.withConfiguration(new SphereReplaceConfig(Blocks.GRAVEL.getDefaultState(), FeatureSpread.func_242253_a(2, 3), 2,
								ImmutableList.of(${mappedBlockToBlockStateCode(data.groundBlock)}, ${mappedBlockToBlockStateCode(data.undergroundBlock)})))
								.withPlacement(Features.Placements.SEAGRASS_DISK_PLACEMENT).func_242731_b(${data.gravelPatchesPerChunk}));
				</#if>

				<#if (data.reedsPerChunk > 0)>
				biomeGenerationSettings.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
						Feature.RANDOM_PATCH.withConfiguration(Features.Configs.SUGAR_CANE_PATCH_CONFIG)
								.withPlacement(Features.Placements.PATCH_PLACEMENT).func_242731_b(${data.reedsPerChunk}));
				</#if>

				<#if (data.cactiPerChunk > 0)>
				biomeGenerationSettings.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
						Feature.RANDOM_PATCH.withConfiguration((new BlockClusterFeatureConfig.Builder(
								new SimpleBlockStateProvider(Blocks.CACTUS.getDefaultState()), new ColumnBlockPlacer(1, 2)))
								.tries(${data.cactiPerChunk}).func_227317_b_().build()));
				</#if>

				<#list data.defaultFeatures as defaultFeature>
					<#assign mfeat = generator.map(defaultFeature, "defaultfeatures")>
					<#if mfeat != "null">
						DefaultBiomeFeatures.with${mfeat}(biomeGenerationSettings);
					</#if>
				</#list>

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

	<#if (data.treeVines?has_content && !data.treeVines.isEmpty())>
	private static class CustomLeaveVineTreeDecorator extends LeaveVineTreeDecorator {

		public static final CustomLeaveVineTreeDecorator instance = new CustomLeaveVineTreeDecorator();
		public static com.mojang.serialization.Codec<LeaveVineTreeDecorator> codec;
		public static TreeDecoratorType tdt;

		static {
			codec = com.mojang.serialization.Codec.unit(() -> instance);
			tdt = new TreeDecoratorType(codec);
			tdt.setRegistryName("${registryname}_lvtd");
			ForgeRegistries.TREE_DECORATOR_TYPES.register(tdt);
		}

		@Override protected TreeDecoratorType<?> func_230380_a_() {
			return tdt;
		}

		@Override protected void func_227424_a_(IWorldWriter ww, BlockPos bp, BooleanProperty bpr, Set<BlockPos> sbc, MutableBoundingBox mbb) {
			this.func_227423_a_(ww, bp, ${mappedBlockToBlockStateCode(data.treeVines)}, sbc, mbb);
		}

	}

	private static class CustomTrunkVineTreeDecorator extends TrunkVineTreeDecorator {

		public static final CustomTrunkVineTreeDecorator instance = new CustomTrunkVineTreeDecorator();
		public static com.mojang.serialization.Codec<CustomTrunkVineTreeDecorator> codec;
		public static TreeDecoratorType tdt;

		static {
			codec = com.mojang.serialization.Codec.unit(() -> instance);
			tdt = new TreeDecoratorType(codec);
			tdt.setRegistryName("${registryname}_tvtd");
			ForgeRegistries.TREE_DECORATOR_TYPES.register(tdt);
		}

		@Override protected TreeDecoratorType<?> func_230380_a_() {
			return tdt;
		}

		@Override protected void func_227424_a_(IWorldWriter ww, BlockPos bp, BooleanProperty bpr, Set<BlockPos> sbc, MutableBoundingBox mbb) {
			this.func_227423_a_(ww, bp, ${mappedBlockToBlockStateCode(data.treeVines)}, sbc, mbb);
		}

	}
	</#if>

	<#if (data.treeFruits?has_content && !data.treeFruits.isEmpty())>
	private static class CustomCocoaTreeDecorator extends CocoaTreeDecorator {

		public static final CustomCocoaTreeDecorator instance = new CustomCocoaTreeDecorator();
		public static com.mojang.serialization.Codec<CustomCocoaTreeDecorator> codec;
		public static TreeDecoratorType tdt;

		static {
			codec = com.mojang.serialization.Codec.unit(() -> instance);
			tdt = new TreeDecoratorType(codec);
			tdt.setRegistryName("${registryname}_ctd");
			ForgeRegistries.TREE_DECORATOR_TYPES.register(tdt);
		}

		public CustomCocoaTreeDecorator() {
			super(0.2f);
		}

		@Override protected TreeDecoratorType<?> func_230380_a_() {
			return tdt;
		}

		@Override ${mcc.getMethod("net.minecraft.world.gen.treedecorator.CocoaTreeDecorator", "func_225576_a_", "ISeedReader", "Random", "List", "List", "Set", "MutableBoundingBox")
			.replace("this.field_227417_b_", "0.2F")
			.replace("Blocks.COCOA.getDefaultState().with(CocoaBlock.AGE,Integer.valueOf(p_225576_2_.nextInt(3))).with(CocoaBlock.HORIZONTAL_FACING,direction)",
				mappedBlockToBlockStateCode(data.treeFruits))}

	}
	</#if>

}

<#macro vinesAndCocoa>
.setDecorators(ImmutableList.of(
	<#if (data.treeVines?has_content && !data.treeVines.isEmpty())>
		CustomLeaveVineTreeDecorator.instance,
		CustomTrunkVineTreeDecorator.instance
	</#if>

	<#if (data.treeFruits?has_content && !data.treeFruits.isEmpty())>
		<#if (data.treeVines?has_content && !data.treeVines.isEmpty())>,</#if>
		new CustomCocoaTreeDecorator()
	</#if>
))
</#macro>
<#-- @formatter:on -->