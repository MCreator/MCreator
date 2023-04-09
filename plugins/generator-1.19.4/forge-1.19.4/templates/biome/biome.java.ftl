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
