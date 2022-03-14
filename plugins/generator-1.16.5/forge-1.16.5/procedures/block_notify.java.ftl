<#include "mcelements.ftl">
if(world instanceof World)
    ((World) world).notifyNeighborsOfStateChange(${toBlockPos(input$x,input$y,input$z)},
        ((World) world).getBlockState(${toBlockPos(input$x,input$y,input$z)}).getBlock());