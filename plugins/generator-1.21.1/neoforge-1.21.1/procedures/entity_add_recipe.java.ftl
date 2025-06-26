<#include "mcelements.ftl">
<@definePart type="head">
if(${input$entity} instanceof ServerPlayer _serverPlayer) {
</@definePart>
	_serverPlayer.awardRecipesByKey(Collections.singletonList(${toResourceLocation(input$recipe)}));
<@definePart type="tail">
}
</@definePart>