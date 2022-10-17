<#include "mcelements.ftl">
if(${input$entity} instanceof ServerPlayer _serverPlayer) _serverPlayer.awardRecipesByKey(new ResourceLocation[]{${toResourceLocation(input$recipe)}});