<#include "mcelements.ftl">
<@head>if (${input$entity} instanceof ServerPlayer _serverPlayer) {</@head>
	_serverPlayer.server.getRecipeManager().byKey(${toResourceLocation(input$recipe)}).ifPresent(_rec -> _serverPlayer.resetRecipes(Collections.singleton(_rec)));
<@tail>}</@tail>