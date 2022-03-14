<#include "mcelements.ftl">
<#include "mcitems.ftl">
world.setBlockState(${toBlockPos(input$x,input$y,input$z)}, ${mappedBlockToBlockStateCode(input$block)},3);