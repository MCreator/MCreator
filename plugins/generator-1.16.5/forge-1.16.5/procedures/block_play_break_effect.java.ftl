<#include "mcelements.ftl">
<#include "mcitems.ftl">
world.playEvent(2001, ${toBlockPos(input$x,input$y,input$z)}, Block.getStateId(${mappedBlockToBlockStateCode(input$block)}));