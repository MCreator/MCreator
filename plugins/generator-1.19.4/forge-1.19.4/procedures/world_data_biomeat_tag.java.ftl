<#if generator.map(field$biometag, "tags")?has_content>
	<#if generator.map(field$biometag, "tags")??>
	(world.getBiome(${toBlockPos(input$x,input$y,input$z)}).is(TagKey.create(Registry.BIOME, new ResourceLocation(${generator.map(field$biometag, "tags")?replace("CUSTOM:", "")}"))))
	<#else>
	(world.getBiome(${toBlockPos(input$x,input$y,input$z)}).is(TagKey.create(Registry.BIOME, new ResourceLocation(${field$biometag}"))))
	</#if>
<#else>(false)</#if>