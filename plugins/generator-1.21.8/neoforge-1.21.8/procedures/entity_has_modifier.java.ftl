<#include "mcelements.ftl">
(${input$entity} instanceof LivingEntity _livingEntity${cbi} && _livingEntity${cbi}.getAttribute(${generator.map(field$attribute, "attributes")})
	.hasModifier(${toResourceLocation('"' + modid + ':' + field$name + '"')}))