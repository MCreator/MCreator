<#include "mcelements.ftl">
(world.getBiome(${toBlockPos(input$x,input$y,input$z)}).is(new ResourceLocation("${generator.map(field$biome, "biomes")}")))