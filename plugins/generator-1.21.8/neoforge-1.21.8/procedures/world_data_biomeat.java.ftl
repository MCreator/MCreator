<#include "mcelements.ftl">
(world.getBiome(${toBlockPos(input$x,input$y,input$z)}).is(ResourceLocation.parse("${generator.map(field$biome, "biomes")}")))