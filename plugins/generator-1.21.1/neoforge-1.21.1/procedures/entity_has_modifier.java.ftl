<#include "mcelements.ftl">
<#assign modifier = toResourceLocation('"' + modid + ':" +' + input$name?replace(' ', ''))>
(${input$entity} instanceof LivingEntity _livingEntity${cbi} && _livingEntity${cbi}.getAttribute(${generator.map(field$attribute, "attributes")}).hasModifier(${modifier}))