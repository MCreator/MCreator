<#if field$damagesource?has_content>
	${generator.map(field$damagesource, "damagesources")}
<#else>
	DamageSource.GENERIC
</#if>