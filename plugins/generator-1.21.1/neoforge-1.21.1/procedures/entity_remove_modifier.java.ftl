<#include "mcelements.ftl">
if (${input$entity} instanceof LivingEntity _entity) {
	_entity.getAttribute(${generator.map(field$attribute, "attributes")}).removeModifier(${toResourceLocation('"' + modid + ':' + field$name + '"')});
}