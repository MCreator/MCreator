(world.getBiome(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z})).getRegistryName()
        .equals(new ResourceLocation("${generator.map(field$biome, "biomes")}")))