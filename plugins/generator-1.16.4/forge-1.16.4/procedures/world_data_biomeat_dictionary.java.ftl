(world.func_241828_r().getRegistry(Registry.BIOME_KEY).getKey(world.getBiome(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}))) != null &&
		BiomeDictionary.hasType(RegistryKey.getOrCreateKey(Registry.BIOME_KEY, world.func_241828_r()
		.getRegistry(Registry.BIOME_KEY).getKey(world.getBiome(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z})))),
        BiomeDictionary.Type.${generator.map(field$biomedict, "biomedictionarytypes")}))