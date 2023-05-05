<#include "mcelements.ftl">
if (world.getLevelData() instanceof WritableLevelData _levelData)
	_levelData.setSpawn(${toBlockPos(input$x,input$y,input$z)}, 0);