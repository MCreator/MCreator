<#include "mcelements.ftl">
<@head>if (${input$entity} instanceof ServerPlayer _player) {</@head>
	_serverPlayer.setRespawnPosition(_serverPlayer.level().dimension(), ${toBlockPos(input$x,input$y,input$z)}, _serverPlayer.getYRot(), true, false);
<@tail>}</@tail>