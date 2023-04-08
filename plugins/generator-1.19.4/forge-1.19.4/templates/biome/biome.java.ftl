
		<#if data.ambientSound?has_content && data.ambientSound.getMappedValue()?has_content>
		    .ambientLoopSound(ForgeRegistries.SOUND_EVENTS.getDelegateOrThrow((new ResourceLocation("${data.ambientSound}"))))
		</#if>
		<#if data.moodSound?has_content && data.moodSound.getMappedValue()?has_content>
		    .ambientMoodSound(new AmbientMoodSettings(ForgeRegistries.SOUND_EVENTS.getDelegateOrThrow(new ResourceLocation("${data.moodSound}")), ${data.moodSoundDelay}, 8, 2))
		</#if>
		<#if data.additionsSound?has_content && data.additionsSound.getMappedValue()?has_content>
		    .ambientAdditionsSound(new AmbientAdditionsSettings(ForgeRegistries.SOUND_EVENTS.getDelegateOrThrow(new ResourceLocation("${data.additionsSound}")), 0.0111D))
		</#if>
		<#if data.music?has_content && data.music.getMappedValue()?has_content>
		    .backgroundMusic(new Music(ForgeRegistries.SOUND_EVENTS.getDelegateOrThrow(new ResourceLocation("${data.music}")), 12000, 24000, true))
		</#if>
		<#if data.spawnParticles>
		    .ambientParticle(new AmbientParticleSettings(${data.particleToSpawn}, ${data.particlesProbability / 100}f))
		</#if>

        <#if (data.treesPerChunk > 0)>
        	<#assign ct = data.treeType == data.TREES_CUSTOM>

            biomeGenerationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacementUtils.register("${modid}:tree_${registryname}",
                FeatureUtils.register("${modid}:tree_${registryname}", Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder
        	    <#if data.vanillaTreeType == "Big trees">
        	        (
			    	    BlockStateProvider.simple(${ct?then(mappedBlockToBlockStateCode(data.treeStem), "Blocks.JUNGLE_LOG.defaultBlockState()")}),
			    		new MegaJungleTrunkPlacer(${ct?then([data.minHeight, 32]?min, 10)}, 2, 19),
			    		BlockStateProvider.simple(${ct?then(mappedBlockToBlockStateCode(data.treeBranch), "Blocks.JUNGLE_LEAVES.defaultBlockState()")}),
			    		new MegaJungleFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 2),
			    		new TwoLayersFeatureSize(1, 1, 2)
                    )
                    <#if data.hasVines() || data.hasFruits()>
                        <@vinesAndFruits/>
                    <#else>
                        .ignoreVines()
                    </#if>
                <#elseif data.vanillaTreeType == "Savanna trees">
                    (
                        BlockStateProvider.simple(${ct?then(mappedBlockToBlockStateCode(data.treeStem), "Blocks.ACACIA_LOG.defaultBlockState()")}),
                        new ForkingTrunkPlacer(${ct?then([data.minHeight, 32]?min, 5)}, 2, 2),
                        BlockStateProvider.simple(${ct?then(mappedBlockToBlockStateCode(data.treeBranch), "Blocks.ACACIA_LEAVES.defaultBlockState()")}),
                        new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0)),
                        new TwoLayersFeatureSize(1, 0, 2)
                    )
                    <#if data.hasVines() || data.hasFruits()>
                        <@vinesAndFruits/>
                    <#else>
                        .ignoreVines()
                    </#if>
                <#elseif data.vanillaTreeType == "Mega pine trees">
                    (
                        BlockStateProvider.simple(${ct?then(mappedBlockToBlockStateCode(data.treeStem), "Blocks.SPRUCE_LOG.defaultBlockState()")}),
                        new GiantTrunkPlacer(${ct?then([data.minHeight, 32]?min, 13)}, 2, 14),
                        BlockStateProvider.simple(${ct?then(mappedBlockToBlockStateCode(data.treeBranch), "Blocks.SPRUCE_LEAVES.defaultBlockState()")}),
                        new MegaPineFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0), UniformInt.of(3, 4)),
                        new TwoLayersFeatureSize(1, 1, 2)
                    )
                    <#if data.hasVines() || data.hasFruits()>
                        <@vinesAndFruits/>
                    </#if>
                <#elseif data.vanillaTreeType == "Mega spruce trees">
                    (
                        BlockStateProvider.simple(${ct?then(mappedBlockToBlockStateCode(data.treeStem), "Blocks.SPRUCE_LOG.defaultBlockState()")}),
                        new GiantTrunkPlacer(${ct?then([data.minHeight, 32]?min, 13)}, 2, 14),
                        BlockStateProvider.simple(${ct?then(mappedBlockToBlockStateCode(data.treeBranch), "Blocks.SPRUCE_LEAVES.defaultBlockState()")}),
                        new MegaPineFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0), UniformInt.of(13, 17)),
                        new TwoLayersFeatureSize(1, 1, 2)
                    )
                    .decorators(ImmutableList.of(new AlterGroundDecorator(BlockStateProvider.simple(Blocks.PODZOL.defaultBlockState()))))
                    <#if data.hasVines() || data.hasFruits()>
                        <@vinesAndFruits/>
                    </#if>
                <#elseif data.vanillaTreeType == "Birch trees">
                    (
                        BlockStateProvider.simple(${ct?then(mappedBlockToBlockStateCode(data.treeStem), "Blocks.BIRCH_LOG.defaultBlockState()")}),
                        new StraightTrunkPlacer(${ct?then([data.minHeight, 32]?min, 5)}, 2, 0),
                        BlockStateProvider.simple(${ct?then(mappedBlockToBlockStateCode(data.treeBranch), "Blocks.BIRCH_LEAVES.defaultBlockState()")}),
                        new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                        new TwoLayersFeatureSize(1, 0, 1)
                    )
                    <#if data.hasVines() || data.hasFruits()>
                        <@vinesAndFruits/>
                    <#else>
                        .ignoreVines()
                    </#if>
                <#else>
                    (
                        BlockStateProvider.simple(${ct?then(mappedBlockToBlockStateCode(data.treeStem), "Blocks.OAK_LOG.defaultBlockState()")}),
                        new StraightTrunkPlacer(${ct?then([data.minHeight, 32]?min, 4)}, 2, 0),
                        BlockStateProvider.simple(${ct?then(mappedBlockToBlockStateCode(data.treeBranch), "Blocks.OAK_LEAVES.defaultBlockState()")}),
                        new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                        new TwoLayersFeatureSize(1, 0, 1)
                    )
                    <#if data.hasVines() || data.hasFruits()>
                        <@vinesAndFruits/>
                    <#else>
                        .ignoreVines()
                    </#if>
                </#if>
            .build()), List.of(
				CountPlacement.of(${data.treesPerChunk}),
                InSquarePlacement.spread(),
                SurfaceWaterDepthFilter.forMaxDepth(0),
                PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
                PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING),
				BiomeFilter.biome()
            )));
        </#if>

        <#if (data.grassPerChunk > 0)>
            biomeGenerationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                PlacementUtils.register("${modid}:grass_${registryname}", VegetationFeatures.PATCH_GRASS, List.of(
				    NoiseThresholdCountPlacement.of(-0.8D, 5, ${data.grassPerChunk}),
                    InSquarePlacement.spread(),
                    PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                    BiomeFilter.biome()
            )));
        </#if>

        <#if (data.seagrassPerChunk > 0)>
            biomeGenerationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
			    PlacementUtils.register("${modid}:seagrass_${registryname}", AquaticFeatures.SEAGRASS_SHORT,
                        AquaticPlacements.seagrassPlacement(${data.seagrassPerChunk})
            ));
        </#if>

        <#if (data.flowersPerChunk > 0)>
            biomeGenerationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
			    PlacementUtils.register("${modid}:flower_${registryname}", VegetationFeatures.FLOWER_DEFAULT, List.of(
				    CountPlacement.of(${data.flowersPerChunk}),
                    RarityFilter.onAverageOnceEvery(32),
                    InSquarePlacement.spread(),
                    PlacementUtils.HEIGHTMAP,
                    BiomeFilter.biome()
            )));
        </#if>

        <#if (data.mushroomsPerChunk > 0)>
            biomeGenerationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
			    PlacementUtils.register("${modid}:brown_mushroom_${registryname}", VegetationFeatures.PATCH_BROWN_MUSHROOM, List.of(
				    CountPlacement.of(${data.mushroomsPerChunk}),
				    RarityFilter.onAverageOnceEvery(32),
				    InSquarePlacement.spread(),
				    PlacementUtils.HEIGHTMAP,
				    BiomeFilter.biome()
			)));

            biomeGenerationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
			    PlacementUtils.register("${modid}:red_mushroom_${registryname}", VegetationFeatures.PATCH_RED_MUSHROOM, List.of(
				    CountPlacement.of(${data.mushroomsPerChunk}),
				    RarityFilter.onAverageOnceEvery(32),
				    InSquarePlacement.spread(),
				    PlacementUtils.HEIGHTMAP,
				    BiomeFilter.biome()
			)));
        </#if>

        <#if (data.bigMushroomsChunk > 0)>
            biomeGenerationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
			    PlacementUtils.register("${modid}:mushrooms_huge_${registryname}", VegetationFeatures.MUSHROOM_ISLAND_VEGETATION, List.of(
				    CountPlacement.of(${data.bigMushroomsChunk}),
				    InSquarePlacement.spread(),
				    PlacementUtils.HEIGHTMAP,
				    BiomeFilter.biome()
            )));
        </#if>

        <#if (data.reedsPerChunk > 0)>
            biomeGenerationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
			    PlacementUtils.register("${modid}:patch_sugar_cane_${registryname}", VegetationFeatures.PATCH_SUGAR_CANE, List.of(
				    RarityFilter.onAverageOnceEvery(${data.reedsPerChunk}),
                    InSquarePlacement.spread(),
                    PlacementUtils.HEIGHTMAP,
                    BiomeFilter.biome()
            )));
        </#if>

        <#if (data.cactiPerChunk > 0)>
            biomeGenerationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
			    PlacementUtils.register("${modid}:patch_cactus_${registryname}", VegetationFeatures.PATCH_SUGAR_CANE, List.of(
				    RarityFilter.onAverageOnceEvery(${data.cactiPerChunk}),
				    InSquarePlacement.spread(),
				    PlacementUtils.HEIGHTMAP,
				    BiomeFilter.biome()
			)));
        </#if>

        <#if (data.sandPatchesPerChunk > 0)>
            biomeGenerationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
			    PlacementUtils.register("${modid}:disk_sand_${registryname}", FeatureUtils.register("${modid}:disk_sand_${registryname}",
                        Feature.DISK, new DiskConfiguration(RuleBasedBlockStateProvider.simple(Blocks.SAND),
                        BlockPredicate.matchesBlocks(List.of(${mappedBlockToBlock(data.groundBlock)}, ${mappedBlockToBlock(data.undergroundBlock)})),
                        UniformInt.of(2, 6), 2
                    )), List.of(
				        CountPlacement.of(${data.sandPatchesPerChunk}),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID,
                        BiomeFilter.biome()
            )));
        </#if>

        <#if (data.gravelPatchesPerChunk > 0)>
            biomeGenerationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
			    PlacementUtils.register("${modid}:disk_gravel_${registryname}", FeatureUtils.register("${modid}:disk_gravel_${registryname}",
			        Feature.DISK, new DiskConfiguration(RuleBasedBlockStateProvider.simple(Blocks.GRAVEL),
                    BlockPredicate.matchesBlocks(List.of(${mappedBlockToBlock(data.groundBlock)}, ${mappedBlockToBlock(data.undergroundBlock)})),
                    UniformInt.of(2, 5), 2
			    )), List.of(
			        CountPlacement.of(${data.gravelPatchesPerChunk}),
			        InSquarePlacement.spread(),
			        PlacementUtils.HEIGHTMAP_TOP_SOLID,
			        BiomeFilter.biome()
            )));
        </#if>

        <#list generator.sortByMappings(data.defaultFeatures, "defaultfeatures") as defaultFeature>
            <#if data.spawnBiomeNether &&
                    (defaultFeature == "Caves" ||
                     defaultFeature == "ExtraEmeraldOre" ||
                     defaultFeature == "ExtraGoldOre" ||
                     defaultFeature == "Ores" ||
                     defaultFeature == "MonsterRooms" ||
                     defaultFeature == "Fossils")>
                <#continue>
            </#if>

        	<#assign mfeat = generator.map(defaultFeature, "defaultfeatures")>
        	<#if mfeat != "null">
            BiomeDefaultFeatures.add${mfeat}(biomeGenerationSettings);
        	</#if>
        </#list>
