<#if generator.map(field$biometag, "tags") != "null">
(world.getBiome(${toBlockPos(input$x,input$y,input$z)}).is(TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(${generator.map(field$biometag, "tags")}"))))
<#else>(false)</#if>