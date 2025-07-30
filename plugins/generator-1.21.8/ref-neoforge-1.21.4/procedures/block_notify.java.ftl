<#include "mcelements.ftl">
if(world instanceof Level _level)
	_level.updateNeighborsAt(${toBlockPos(input$x,input$y,input$z)}, _level.getBlockState(${toBlockPos(input$x,input$y,input$z)}).getBlock());