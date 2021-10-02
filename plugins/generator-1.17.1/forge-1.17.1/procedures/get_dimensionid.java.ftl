<#if field$dimension=="Surface">
	(Level.OVERWORLD)
<#elseif field$dimension=="Nether">
	(Level.THE_NETHER)
<#elseif field$dimension=="End">
	(Level.THE_END)
<#else>
	(ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("${generator.getResourceLocationForModElement(field$dimension.replace("CUSTOM:", ""))}")))
</#if>