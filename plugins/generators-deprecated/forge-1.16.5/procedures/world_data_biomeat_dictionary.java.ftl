<#include "mcelements.ftl">
(world.func_241828_r().getRegistry(Registry.BIOME_KEY).getKey(world.getBiome(${toBlockPos(input$x,input$y,input$z)})) != null &&
		BiomeDictionary.hasType(RegistryKey.getOrCreateKey(Registry.BIOME_KEY, world.func_241828_r()
		.getRegistry(Registry.BIOME_KEY).getKey(world.getBiome(${toBlockPos(input$x,input$y,input$z)}))),
        BiomeDictionary.Type.${generator.map(field$biomedict, "biomedictionarytypes")}))