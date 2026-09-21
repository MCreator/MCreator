<#include "mcelements.ftl">
if (world.getLevelData() instanceof WritableLevelData _levelData && world instanceof Level _level)
	_levelData.setSpawn(LevelData.RespawnData.of(_level.dimension(), ${toBlockPos(input$x,input$y,input$z)}, 0.0F, 0.0F));