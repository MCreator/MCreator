(world.getBiome(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z})).getRegistryName() != null &&
		BiomeDictionary.hasType(RegistryKey.getOrCreateKey(Registry.BIOME_KEY, world.getBiome(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z})).getRegistryName()),
        BiomeDictionary.Type.${generator.map(field$biomedict, "biomedictionarytypes")}))