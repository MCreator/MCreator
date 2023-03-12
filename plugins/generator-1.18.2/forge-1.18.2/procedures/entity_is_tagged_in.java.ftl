<#if generator.map(field$entitytag, "tags") != "null">
(${input$entity}.getType().is(TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(${generator.map(field$entitytag, "tags")}))))
<#else>(false)</#if>