<#if field$dimension=="Surface">
	Level.OVERWORLD
<#elseif field$dimension=="Nether">
	Level.NETHER
<#elseif field$dimension=="End">
	Level.END
<#else>
	(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("${generator.getResourceLocationForModElement(field$dimension.replace("CUSTOM:", ""))}")))
</#if>