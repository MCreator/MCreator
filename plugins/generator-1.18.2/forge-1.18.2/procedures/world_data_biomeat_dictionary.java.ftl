(world.getBiome(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z})).value().getRegistryName() != null &&
		BiomeDictionary.hasType(ResourceKey.create(Registry.BIOME_REGISTRY, world.registryAccess()
		.registryOrThrow(Registry.BIOME_REGISTRY).getKey(world.getBiome(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z})).value())),
        BiomeDictionary.Type.${generator.map(field$biomedict, "biomedictionarytypes")}))