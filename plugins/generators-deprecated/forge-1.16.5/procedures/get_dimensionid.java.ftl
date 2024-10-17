<#if field$dimension=="Surface">
	(World.OVERWORLD)
<#elseif field$dimension=="Nether">
	(World.THE_NETHER)
<#elseif field$dimension=="End">
	(World.THE_END)
<#else>
	(RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation("${generator.getResourceLocationForModElement(field$dimension.replace("CUSTOM:", ""))}")))
</#if>