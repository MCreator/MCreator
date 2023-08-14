<#include "mcelements.ftl">
(world.getBiome(${toBlockPos(input$x,input$y,input$z)}).is(TagKey.create(Registries.BIOME, ${toResourceLocation(input$tag)})))