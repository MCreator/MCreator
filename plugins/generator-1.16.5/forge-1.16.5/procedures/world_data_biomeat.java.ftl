<#include "mcelements.ftl">
(world.func_241828_r().getRegistry(Registry.BIOME_KEY).getKey(world.getBiome(${toBlockPos(input$x,input$y,input$z)})) != null &&
        world.func_241828_r().getRegistry(Registry.BIOME_KEY).getKey(world.getBiome(${toBlockPos(input$x,input$y,input$z)}))
        .equals(new ResourceLocation("${generator.map(field$biome, "biomes")}")))