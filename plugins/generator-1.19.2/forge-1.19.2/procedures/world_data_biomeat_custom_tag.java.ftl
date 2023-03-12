<#include "mcelements.ftl">
(world.getBiome(${toBlockPos(input$x,input$y,input$z)}).is(TagKey.create(Registry.BIOME_REGISTRY, ${toResourceLocation(input$tag)})))