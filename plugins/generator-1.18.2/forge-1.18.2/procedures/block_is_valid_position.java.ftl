<#include "mcelements.ftl">
<#include "mcitems.ftl">
(${mappedBlockToBlockStateCode(input$block)}.canSurvive(world, ${toBlockPos(input$x,input$y,input$z)}))