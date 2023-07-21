<#include "mcelements.ftl">
<#include "mcitems.ftl">
world.levelEvent(2001, ${toBlockPos(input$x,input$y,input$z)}, Block.getId(${mappedBlockToBlockStateCode(input$block)}));