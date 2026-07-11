<#include "mcelements.ftl">
<#include "mcitems.ftl">
world.setBlock(${toBlockPos(input$x,input$y,input$z)}, ${mappedBlockToBlockStateCode(input$block)},3);