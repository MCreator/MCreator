<#include "mcelements.ftl">
(new ResourceLocation("${generator.map(field$biome, "biomes")}").equals(world.getBiome(${toBlockPos(input$x,input$y,input$z)}).value().getRegistryName()))