<#include "mcelements.ftl">
(world.getBiome(${toBlockPos(input$x,input$y,input$z)}).is(Identifier.parse("${generator.map(field$biome, "biomes")}")))