<#include "mcelements.ftl">
<#include "mcitems.ftl">
(${mappedBlockToBlockStateCode(input$block)}.isValidPosition(world, ${toBlockPos(input$x,input$y,input$z)}))