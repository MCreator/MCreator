<#include "mcelements.ftl">
<@head>if(world instanceof Level _level) {</@head>
	_level.updateNeighborsAt(${toBlockPos(input$x,input$y,input$z)}, _level.getBlockState(${toBlockPos(input$x,input$y,input$z)}).getBlock());
<@tail>}</@tail>