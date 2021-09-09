<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2021, Pylo, opensource contributors
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
 # along setValue this program.  If not, see <https://www.gnu.org/licenses/>.
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

package ${package}.world.biome;
<#include "mcitems.ftl">

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD) public class ${name}Biome {

    @SubscribeEvent public void registerBiomes(RegistryEvent.Register<Biome> event) {
    	if (biome == null) {
            BiomeSpecialEffects effects = new BiomeSpecialEffects.Builder()
                .fogColor(${data.airColor?has_content?then(data.airColor.getRGB(), 12638463)})
                .waterColor(${data.waterColor?has_content?then(data.waterColor.getRGB(), 4159204)})
                .waterFogColor(${data.waterFogColor?has_content?then(data.waterFogColor.getRGB(), 329011)})
                .skyColor(${data.airColor?has_content?then(data.airColor.getRGB(), 7972607)})
                .foliageColorOverride(${data.foliageColor?has_content?then(data.foliageColor.getRGB(), 10387789)})
                .grassColorOverride(${data.grassColor?has_content?then(data.grassColor.getRGB(), 9470285)})
                <#if data.ambientSound?has_content && data.ambientSound.getMappedValue()?has_content>
                    .ambientLoopSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("${data.ambientSound}")))
                </#if>
                <#if data.moodSound?has_content && data.moodSound.getMappedValue()?has_content>
                    .ambientMoodSound(new AmbientMoodSettings(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("${data.moodSound}")), ${data.moodSoundDelay}, 8, 2))
                </#if>
                <#if data.additionsSound?has_content && data.additionsSound.getMappedValue()?has_content>
                    .ambientAdditionsSound(new AmbientAdditionsSettings(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("${data.additionsSound}")), 0.0111D))
                </#if>
                <#if data.music?has_content && data.music.getMappedValue()?has_content>
                    .backgroundMusic(new Music(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("${data.music}")), 12000, 24000, true))
                </#if>
                <#if data.spawnParticles>
                    .ambientParticle(new AmbientParticleSettings(${data.particleToSpawn}, ${data.particlesProbability / 100}f))
                </#if>
                .build();

        BiomeGenerationSettings.Builder biomeGenerationSettings = new BiomeGenerationSettings.Builder()
            .surfaceBuilder(SurfaceBuilder.DEFAULT.configured(
                new SurfaceBuilderBaseConfiguration(${mappedBlockToBlockStateCode(data.groundBlock)},
                	${mappedBlockToBlockStateCode(data.undergroundBlock)},
                	${mappedBlockToBlockStateCode(data.undergroundBlock)})));

        <#if data.spawnStronghold>
            biomeGenerationSettings.addStructureStart(StructureFeatures.STRONGHOLD);
        </#if>

        <#if data.spawnMineshaft>
            biomeGenerationSettings.addStructureStart(StructureFeatures.MINESHAFT);
        </#if>

        <#if data.spawnPillagerOutpost>
            biomeGenerationSettings.addStructureStart(StructureFeatures.PILLAGER_OUTPOST);
        </#if>

        <#if data.villageType != "none">
            biomeGenerationSettings.addStructureStart(StructureFeatures.VILLAGE_${data.villageType?upper_case});
        </#if>

        <#if data.spawnWoodlandMansion>
            biomeGenerationSettings.addStructureStart(StructureFeatures.WOODLAND_MANSION);
        </#if>

        <#if data.spawnJungleTemple>
            biomeGenerationSettings.addStructureStart(StructureFeatures.JUNGLE_TEMPLE);
        </#if>

        <#if data.spawnDesertPyramid>
            biomeGenerationSettings.addStructureStart(StructureFeatures.DESERT_PYRAMID);
        </#if>

        <#if data.spawnIgloo>
            biomeGenerationSettings.addStructureStart(StructureFeatures.IGLOO);
        </#if>

        <#if data.spawnOceanMonument>
            biomeGenerationSettings.addStructureStart(StructureFeatures.OCEAN_MONUMENT);
        </#if>

        <#if data.spawnShipwreck>
            biomeGenerationSettings.addStructureStart(StructureFeatures.SHIPWRECK);
        </#if>

        <#if data.oceanRuinType != "NONE">
            biomeGenerationSettings.addStructureStart(StructureFeatures.OCEAN_RUIN_${data.oceanRuinType});
        </#if>

        <#if (data.treesPerChunk > 0)>
        	<#assign ct = data.treeType == data.TREES_CUSTOM>
        	<#if ct>
        	</#if>

        	<#if data.vanillaTreeType == "Big trees">
        	biomeGenerationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
            	Feature.TREE.configured((new TreeConfiguration.TreeConfigurationBuilder(
                new SimpleStateProvider(${ct?then(mappedBlockToBlockStateCode(data.treeStem), "Blocks.JUNGLE_LOG.defaultBlockState()")}),
                new MegaJungleTrunkPlacer(${ct?then(data.minHeight, 10)}, 2, 19),
                new SimpleStateProvider(${ct?then(mappedBlockToBlockStateCode(data.treeBranch), "Blocks.JUNGLE_LEAVES.defaultBlockState()")}),
                new SimpleStateProvider(Blocks.OAK_SAPLING.defaultBlockState()),
                new JungleFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 2),
                new TwoLayersFeatureSize(1, 1, 2)))
                <#if (data.treeVines?has_content && !data.treeVines.isEmpty()) || (data.treeFruits?has_content && !data.treeFruits.isEmpty())>
                	<@vinesAndCocoa/>
                <#else>
                	.setDecorators(ImmutableList.of(TrunkVineTreeDecorator.field_236879_b_, LeaveVineTreeDecorator.field_236871_b_))
                </#if>
                <#if data.treeType == data.TREES_CUSTOM>
                .setMaxWaterDepth(${data.maxWaterDepth})
                </#if>
            	.build())
            	.decorated(Features.Decorators.HEIGHTMAP_SQUARE)
            	.decorated(FeatureDecorator.COUNT_EXTRA.configured(new FrequencyWithExtraChanceDecoratorConfiguration(${data.treesPerChunk}, 0.1F, 1)))
        	);
        	<#elseif data.vanillaTreeType == "Savanna trees">
        	biomeGenerationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
            	Feature.TREE.configured((new TreeConfiguration.TreeConfigurationBuilder(
                new SimpleStateProvider(${ct?then(mappedBlockToBlockStateCode(data.treeStem), "Blocks.ACACIA_LOG.defaultBlockState()")}),
                new ForkyTrunkPlacer(${ct?then(data.minHeight, 5)}, 2, 2),
                new SimpleStateProvider(${ct?then(mappedBlockToBlockStateCode(data.treeBranch), "Blocks.ACACIA_LEAVES.defaultBlockState()")}),
                new SimpleStateProvider(Blocks.ACACIA_SAPLING.defaultBlockState()),
                new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0)),
                new TwoLayersFeatureSize(1, 0, 2)))
                <#if (data.treeVines?has_content && !data.treeVines.isEmpty()) || (data.treeFruits?has_content && !data.treeFruits.isEmpty())>
                	<@vinesAndCocoa/>
                <#else>
                	.ignoreVines()
                </#if>
                <#if data.treeType == data.TREES_CUSTOM>
                .setMaxWaterDepth(${data.maxWaterDepth})
                </#if>
            	.build())
            	.decorated(Features.Decorators.HEIGHTMAP_SQUARE)
            	.decorated(FeatureDecorator.COUNT_EXTRA.configured(new FrequencyWithExtraChanceDecoratorConfiguration(${data.treesPerChunk}, 0.1F, 1)))
        	);
        	<#elseif data.vanillaTreeType == "Mega pine trees">
        	biomeGenerationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
            	Feature.TREE.configured((new TreeConfiguration.TreeConfigurationBuilder(
                new SimpleStateProvider(${ct?then(mappedBlockToBlockStateCode(data.treeStem), "Blocks.SPRUCE_LOG.defaultBlockState()")}),
                new GiantTrunkPlacer(${ct?then(data.minHeight, 13)}, 2, 14),
                new SimpleStateProvider(${ct?then(mappedBlockToBlockStateCode(data.treeBranch), "Blocks.SPRUCE_LEAVES.defaultBlockState()")}),
                new SimpleStateProvider(Blocks.SPRUCE_SAPLING.defaultBlockState()),
                new MegaPineFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0), UniformInt.of(3, 4)),
                new TwoLayersFeatureSize(1, 1, 2)))
                <#if (data.treeVines?has_content && !data.treeVines.isEmpty()) || (data.treeFruits?has_content && !data.treeFruits.isEmpty())>
                	<@vinesAndCocoa/>
                </#if>
                <#if data.treeType == data.TREES_CUSTOM>
                .setMaxWaterDepth(${data.maxWaterDepth})
                </#if>
            	.build())
            	.decorated(Features.Decorators.HEIGHTMAP_SQUARE)
            	.decorated(FeatureDecorator.COUNT_EXTRA.configured(new FrequencyWithExtraChanceDecoratorConfiguration(${data.treesPerChunk}, 0.1F, 1)))
        	);
        	<#elseif data.vanillaTreeType == "Mega spruce trees">
        	biomeGenerationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
            	Feature.TREE.configured((new TreeConfiguration.TreeConfigurationBuilder(
                new SimpleStateProvider(${ct?then(mappedBlockToBlockStateCode(data.treeStem), "Blocks.SPRUCE_LOG.defaultBlockState()")}),
                new GiantTrunkPlacer(${ct?then(data.minHeight, 13)}, 2, 14),
                new SimpleStateProvider(${ct?then(mappedBlockToBlockStateCode(data.treeBranch), "Blocks.SPRUCE_LEAVES.defaultBlockState()")}),
                new SimpleStateProvider(Blocks.SPRUCE_SAPLING.defaultBlockState()),
                new MegaPineFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0), UniformInt.of(13, 4)),
                new TwoLayersFeatureSize(1, 1, 2)))
                <#if (data.treeVines?has_content && !data.treeVines.isEmpty()) || (data.treeFruits?has_content && !data.treeFruits.isEmpty())>
                	<@vinesAndCocoa/>
                </#if>
                <#if data.treeType == data.TREES_CUSTOM>
                .setMaxWaterDepth(${data.maxWaterDepth})
                </#if>
            	.build())
            	.decorated(Features.Decorators.HEIGHTMAP_SQUARE)
            	.decorated(FeatureDecorator.COUNT_EXTRA.configured(new FrequencyWithExtraChanceDecoratorConfiguration(${data.treesPerChunk}, 0.1F, 1)))
        	);
        	<#elseif data.vanillaTreeType == "Birch trees">
        	biomeGenerationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
            	Feature.TREE.configured((new TreeConfiguration.TreeConfigurationBuilder(
                new SimpleStateProvider(${ct?then(mappedBlockToBlockStateCode(data.treeStem), "Blocks.BIRCH_LOG.defaultBlockState()")}),
                new StraightTrunkPlacer(${ct?then(data.minHeight, 5)}, 2, 0),
                new SimpleStateProvider(${ct?then(mappedBlockToBlockStateCode(data.treeBranch), "Blocks.BIRCH_LEAVES.defaultBlockState()")}),
                new SimpleStateProvider(Blocks.BIRCH_SAPLING.defaultBlockState()),
                new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                new TwoLayersFeatureSize(1, 0, 1)))
                <#if (data.treeVines?has_content && !data.treeVines.isEmpty()) || (data.treeFruits?has_content && !data.treeFruits.isEmpty())>
                	<@vinesAndCocoa/>
                <#else>
                	.ignoreVines()
                </#if>
                <#if data.treeType == data.TREES_CUSTOM>
                .setMaxWaterDepth(${data.maxWaterDepth})
                </#if>
            	.build())
            	.decorated(Features.Decorators.HEIGHTMAP_SQUARE)
            	.decorated(FeatureDecorator.COUNT_EXTRA.configured(new FrequencyWithExtraChanceDecoratorConfiguration(${data.treesPerChunk}, 0.1F, 1)))
        	);
        	<#else>
        	biomeGenerationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
            	Feature.TREE.configured((new TreeConfiguration.TreeConfigurationBuilder(
                new SimpleStateProvider(${ct?then(mappedBlockToBlockStateCode(data.treeStem), "Blocks.OAK_LOG.defaultBlockState()")}),
                new StraightTrunkPlacer(${ct?then(data.minHeight, 4)}, 2, 0),
                new SimpleStateProvider(${ct?then(mappedBlockToBlockStateCode(data.treeBranch), "Blocks.OAK_LEAVES.defaultBlockState()")}),
                new SimpleStateProvider(Blocks.OAK_SAPLING.defaultBlockState()),
                new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                new TwoLayersFeatureSize(1, 0, 1)))
                <#if (data.treeVines?has_content && !data.treeVines.isEmpty()) || (data.treeFruits?has_content && !data.treeFruits.isEmpty())>
                	<@vinesAndCocoa/>
                <#else>
                	.ignoreVines()
                </#if>
                <#if data.treeType == data.TREES_CUSTOM>
                .setMaxWaterDepth(${data.maxWaterDepth})
                </#if>
            	.build())
            	.decorated(Features.Decorators.HEIGHTMAP_SQUARE)
            	.decorated(FeatureDecorator.COUNT_EXTRA.configured(new FrequencyWithExtraChanceDecoratorConfiguration(${data.treesPerChunk}, 0.1F, 1)))
        	);
        	</#if>
        </#if>

        <#if (data.grassPerChunk > 0)>
        biomeGenerationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
            Feature.RANDOM_PATCH.configured(Features.Configs.DEFAULT_GRASS_CONFIG)
            .decorated(Features.Decorators.HEIGHTMAP_DOUBLE_SQUARE)
            .decorated(FeatureDecorator.COUNT_NOISE.configured(new NoiseDependantDecoratorConfiguration(-0.8D, 5, ${data.grassPerChunk}))));
        </#if>

        <#if (data.seagrassPerChunk > 0)>
        biomeGenerationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
            Feature.SEAGRASS.configured(new ProbabilityFeatureConfiguration(0.3F))
                .count(${data.seagrassPerChunk})
                .decorated(Features.Decorators.TOP_SOLID_HEIGHTMAP_SQUARE));
        </#if>

        <#if (data.flowersPerChunk > 0)>
        biomeGenerationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
            Feature.FLOWER.configured(Features.Configs.DEFAULT_FLOWER_CONFIG)
                .decorated(Features.Decorators.ADD_32)
                .decorated(Features.Decorators.HEIGHTMAP_SQUARE)
                .count(${data.flowersPerChunk}));
        </#if>

        <#if (data.mushroomsPerChunk > 0)>
        biomeGenerationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
            Feature.RANDOM_PATCH.configured((new RandomPatchConfiguration.GrassConfigurationBuilder(
                new SimpleStateProvider(Blocks.BROWN_MUSHROOM.defaultBlockState()), SimpleBlockPlacer.INSTANCE))
                .tries(${data.mushroomsPerChunk}).noProjection().build()));
        biomeGenerationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
            Feature.RANDOM_PATCH.configured((new RandomPatchConfiguration.GrassConfigurationBuilder(
                new SimpleStateProvider(Blocks.RED_MUSHROOM.defaultBlockState()), SimpleBlockPlacer.INSTANCE))
                .tries(${data.mushroomsPerChunk}).noProjection().build()));
        </#if>

        <#if (data.bigMushroomsChunk > 0)>
        biomeGenerationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
            Feature.HUGE_BROWN_MUSHROOM.configured(new HugeMushroomFeatureConfiguration(
                new SimpleStateProvider(Blocks.BROWN_MUSHROOM_BLOCK.defaultBlockState().setValue(HugeMushroomBlock.UP, Boolean.TRUE).setValue(HugeMushroomBlock.DOWN, Boolean.FALSE)),
                new SimpleStateProvider(Blocks.MUSHROOM_STEM.defaultBlockState().setValue(HugeMushroomBlock.UP, Boolean.FALSE).setValue(HugeMushroomBlock.DOWN, Boolean.FALSE)), ${data.bigMushroomsChunk})));
        biomeGenerationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
            Feature.HUGE_RED_MUSHROOM.configured(new HugeMushroomFeatureConfiguration(
                new SimpleStateProvider(Blocks.RED_MUSHROOM_BLOCK.defaultBlockState().setValue(HugeMushroomBlock.DOWN, Boolean.FALSE)),
                new SimpleStateProvider(Blocks.MUSHROOM_STEM.defaultBlockState().setValue(HugeMushroomBlock.UP, Boolean.FALSE).setValue(HugeMushroomBlock.DOWN, Boolean.FALSE)), ${data.bigMushroomsChunk})));
        </#if>

        <#if (data.sandPatchesPerChunk > 0)>
        biomeGenerationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
            Feature.DISK.configured(new DiskConfiguration(Blocks.SAND.defaultBlockState(), UniformInt.of(2, 4), 2,
                ImmutableList.of(${mappedBlockToBlockStateCode(data.groundBlock)}, ${mappedBlockToBlockStateCode(data.undergroundBlock)})))
                .decorated(Features.Decorators.TOP_SOLID_HEIGHTMAP_SQUARE).count(${data.sandPatchesPerChunk}));
        </#if>

        <#if (data.gravelPatchesPerChunk > 0)>
        biomeGenerationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
            Feature.DISK.configured(new DiskConfiguration(Blocks.GRAVEL.defaultBlockState(), UniformInt.of(2, 3), 2,
                ImmutableList.of(${mappedBlockToBlockStateCode(data.groundBlock)}, ${mappedBlockToBlockStateCode(data.undergroundBlock)})))
                .decorated(Features.Decorators.TOP_SOLID_HEIGHTMAP_SQUARE).count(${data.gravelPatchesPerChunk}));
        </#if>

        <#if (data.reedsPerChunk > 0)>
        biomeGenerationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
            Feature.RANDOM_PATCH.configured(Features.Configs.SUGAR_CANE_CONFIG)
                .decorated(Features.Decorators.HEIGHTMAP_DOUBLE_SQUARE).count(${data.reedsPerChunk}));
        </#if>

        <#if (data.cactiPerChunk > 0)>
        biomeGenerationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
            Feature.RANDOM_PATCH.configured((new RandomPatchConfiguration.GrassConfigurationBuilder(
                new SimpleStateProvider(Blocks.CACTUS.defaultBlockState()), new ColumnPlacer(BiasedToBottomInt.of(1, 2))))
                .tries(${data.cactiPerChunk}).noProjection().build()));
        </#if>

        <#list data.defaultFeatures as defaultFeature>
        	<#assign mfeat = generator.map(defaultFeature, "defaultfeatures")>
        	<#if mfeat != "null">
            BiomeDefaultFeatures.add${mfeat}(biomeGenerationSettings);
        	</#if>
        </#list>

        MobSpawnSettings.Builder mobSpawnInfo = new MobSpawnSettings.Builder().setPlayerCanSpawn();
        <#list data.spawnEntries as spawnEntry>
        	<#assign entity = generator.map(spawnEntry.entity.getUnmappedValue(), "entities", 1)!"null">
        	<#if entity != "null">
            <#if !entity.toString().contains(".CustomEntity")>
            mobSpawnInfo.addSpawn(${generator.map(spawnEntry.spawnType, "mobspawntypes")}, new MobSpawnSettings.SpawnerData(${entity}, ${spawnEntry.weight}, ${spawnEntry.minGroup}, ${spawnEntry.maxGroup}));
            <#else>
            mobSpawnInfo.addSpawn(${generator.map(spawnEntry.spawnType, "mobspawntypes")}, new MobSpawnSettings.SpawnerData(${entity.toString().replace(".CustomEntity", "")}.entity, ${spawnEntry.weight}, ${spawnEntry.minGroup}, ${spawnEntry.maxGroup}));
            </#if>
        	</#if>
        </#list>

        ${name} = new Biome.BiomeBuilder()
            .precipitation(Biome.Precipitation.<#if (data.rainingPossibility > 0)><#if (data.temperature > 0.15)>RAIN<#else>SNOW</#if><#else>NONE</#if>)
            .biomeCategory(Biome.BiomeCategory.${data.biomeCategory})
            .depth(${data.baseHeight}f)
            .scale(${data.heightVariation}f)
            .temperature(${data.temperature}f)
            .downfall(${data.rainingPossibility}f)
            .specialEffects(effects)
            .mobSpawnSettings(mobSpawnInfo.build())
            .generationSettings(biomeGenerationSettings.build())
            .build();

        event.getRegistry().register(biome.setRegistryName("${modid}:${registryname}"));
    	}
    }

	@SubscribeEvent public void init(FMLCommonSetupEvent event) {
    <#if data.biomeDictionaryTypes?has_content>
    	BiomeDictionary.addTypes(ResourceKey.create(Registry.BIOME_REGISTRY, BuiltinRegistries.BIOME.getKey(biome)),
    	<#list data.biomeDictionaryTypes as biomeDictionaryType>
        BiomeDictionary.Type.${generator.map(biomeDictionaryType, "biomedictionarytypes")}<#if biomeDictionaryType?has_next>,</#if>
    	</#list>
    	);
    </#if>
    <#if data.spawnBiome>
        BiomeManager.addBiome(BiomeManager.BiomeType.${data.biomeType},
            new BiomeManager.BiomeEntry(ResourceKey.create(Registry.BIOME_REGISTRY, BuiltinRegistries.BIOME.getKey(biome)), ${data.biomeWeight}));
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
    	.replace("Blocks.COCOA.defaultBlockState().setValue(CocoaBlock.AGE,Integer.valueOf(p_225576_2_.nextInt(3))).setValue(CocoaBlock.HORIZONTAL_FACING,direction)",
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
