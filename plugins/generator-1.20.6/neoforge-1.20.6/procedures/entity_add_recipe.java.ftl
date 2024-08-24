<#include "mcelements.ftl">
if(${input$entity} instanceof ServerPlayer _serverPlayer) _serverPlayer.awardRecipesByKey(Collections.singletonList(${toResourceLocation(input$recipe)}));