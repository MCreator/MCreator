new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(
<#if field$damagesource?has_content>
	${generator.map(field$damagesource, "damagesources")}
<#else>
	DamageTypes.GENERIC
</#if>
))