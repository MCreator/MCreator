<#if data.vanillaTreeType == "Big trees">
	    BlockStateProvider.simple(${ct?then(mappedBlockToBlockStateCode(data.treeStem), "Blocks.JUNGLE_LOG.defaultBlockState()")}),
		new MegaJungleTrunkPlacer(${ct?then([data.minHeight, 32]?min, 10)}, 2, 19),
		BlockStateProvider.simple(${ct?then(mappedBlockToBlockStateCode(data.treeBranch), "Blocks.JUNGLE_LEAVES.defaultBlockState()")}),
		new MegaJungleFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 2),
		new TwoLayersFeatureSize(1, 1, 2)
<#elseif data.vanillaTreeType == "Savanna trees">
        BlockStateProvider.simple(${ct?then(mappedBlockToBlockStateCode(data.treeStem), "Blocks.ACACIA_LOG.defaultBlockState()")}),
        new ForkingTrunkPlacer(${ct?then([data.minHeight, 32]?min, 5)}, 2, 2),
        BlockStateProvider.simple(${ct?then(mappedBlockToBlockStateCode(data.treeBranch), "Blocks.ACACIA_LEAVES.defaultBlockState()")}),
        new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0)),
        new TwoLayersFeatureSize(1, 0, 2)
<#elseif data.vanillaTreeType == "Mega pine trees">
        BlockStateProvider.simple(${ct?then(mappedBlockToBlockStateCode(data.treeStem), "Blocks.SPRUCE_LOG.defaultBlockState()")}),
        new GiantTrunkPlacer(${ct?then([data.minHeight, 32]?min, 13)}, 2, 14),
        BlockStateProvider.simple(${ct?then(mappedBlockToBlockStateCode(data.treeBranch), "Blocks.SPRUCE_LEAVES.defaultBlockState()")}),
        new MegaPineFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0), UniformInt.of(3, 4)),
        new TwoLayersFeatureSize(1, 1, 2)
<#elseif data.vanillaTreeType == "Mega spruce trees">
        BlockStateProvider.simple(${ct?then(mappedBlockToBlockStateCode(data.treeStem), "Blocks.SPRUCE_LOG.defaultBlockState()")}),
        new GiantTrunkPlacer(${ct?then([data.minHeight, 32]?min, 13)}, 2, 14),
        BlockStateProvider.simple(${ct?then(mappedBlockToBlockStateCode(data.treeBranch), "Blocks.SPRUCE_LEAVES.defaultBlockState()")}),
        new MegaPineFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0), UniformInt.of(13, 17)),
        new TwoLayersFeatureSize(1, 1, 2)
<#elseif data.vanillaTreeType == "Birch trees">
        BlockStateProvider.simple(${ct?then(mappedBlockToBlockStateCode(data.treeStem), "Blocks.BIRCH_LOG.defaultBlockState()")}),
        new StraightTrunkPlacer(${ct?then([data.minHeight, 32]?min, 5)}, 2, 0),
        BlockStateProvider.simple(${ct?then(mappedBlockToBlockStateCode(data.treeBranch), "Blocks.BIRCH_LEAVES.defaultBlockState()")}),
        new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
        new TwoLayersFeatureSize(1, 0, 1)
</#if>