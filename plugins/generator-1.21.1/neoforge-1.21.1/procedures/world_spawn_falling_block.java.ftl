<#include "mcelements.ftl">
<#include "mcitems.ftl">
<@head>if (world instanceof ServerLevel _level) {</@head>
	FallingBlockEntity.fall(_level, ${toBlockPos(input$x,input$y,input$z)}, ${mappedBlockToBlockStateCode(input$block)});
<@tail>}</@tail>