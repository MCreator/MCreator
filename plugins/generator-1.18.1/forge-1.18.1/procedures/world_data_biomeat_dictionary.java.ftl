(world.getBiome(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z})).getRegistryName() != null &&
		BiomeDictionary.hasType(ResourceKey.create(Registry.BIOME_REGISTRY, world.registryAccess()
		.registryOrThrow(Registry.BIOME_REGISTRY).getKey(world.getBiome(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z})))),
        BiomeDictionary.Type.${generator.map(field$biomedict, "biomedictionarytypes")}))