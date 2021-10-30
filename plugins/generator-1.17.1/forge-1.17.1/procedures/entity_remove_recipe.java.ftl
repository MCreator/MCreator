<#include "mcelements.ftl">
if (${input$entity} instanceof ServerPlayer _serverPlayer)
    _serverPlayer.server.getRecipeManager().byKey(${toResourceLocation(input$recipe)}).ifPresent(_rec -> _serverPlayer.resetRecipes(Collections.singleton(_rec)));
