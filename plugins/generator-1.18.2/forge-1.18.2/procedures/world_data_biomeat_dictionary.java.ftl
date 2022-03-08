<#include "mcelements.ftl">
(world.getBiome(${toBlockPos(input$x,input$y,input$z)}).value().getRegistryName() != null &&
		BiomeDictionary.hasType(ResourceKey.create(Registry.BIOME_REGISTRY, world.registryAccess()
		.registryOrThrow(Registry.BIOME_REGISTRY).getKey(world.getBiome(${toBlockPos(input$x,input$y,input$z)}).value())),
        BiomeDictionary.Type.${generator.map(field$biomedict, "biomedictionarytypes")}))