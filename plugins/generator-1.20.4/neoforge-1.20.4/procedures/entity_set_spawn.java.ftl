<#include "mcelements.ftl">
if (${input$entity} instanceof ServerPlayer _serverPlayer)
	_serverPlayer.setRespawnPosition(_serverPlayer.level().dimension(), ${toBlockPos(input$x,input$y,input$z)}, _serverPlayer.getYRot(), true, false);