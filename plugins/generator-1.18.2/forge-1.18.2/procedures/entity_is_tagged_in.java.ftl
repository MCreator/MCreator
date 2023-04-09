<#if generator.map(field$entitytag, "tags")?has_content>
	<#if generator.map(field$entitytag, "tags")??>
	(${input$entity}.getType().is(TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(${generator.map(field$entitytag, "tags")?replace("CUSTOM:", "")}"))))
	<#else>
	(${input$entity}.getType().is(TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(${field$entitytag}"))))
	</#if>
<#else>(false)</#if>