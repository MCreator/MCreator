<#include "mcelements.ftl">
<@head>if(${input$entity} instanceof ServerPlayer _serverPlayer) {</@head>
	_serverPlayer.awardRecipesByKey(Collections.singletonList(${toResourceLocation(input$recipe)}));
<@tail>}</@tail>