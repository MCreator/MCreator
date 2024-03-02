<#include "mcelements.ftl">
<#include "mcitems.ftl">
if (world instanceof ServerLevel _level)
	FallingBlockEntity.fall(_level, ${toBlockPos(input$x,input$y,input$z)}, ${mappedBlockToBlockStateCode(input$block)});