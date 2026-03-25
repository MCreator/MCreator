<#include "mcelements.ftl">
if(${input$entity} instanceof ServerPlayer _serverPlayer)
	_serverPlayer.awardRecipesByKey(Collections.singletonList(ResourceKey.create(Registries.RECIPE, ${toResourceLocation(input$recipe)})));