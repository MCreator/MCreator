<#include "mcelements.ftl">
(world.getBlockState(${toBlockPos(input$x,input$y,input$z)})
        .getOpacity(world,${toBlockPos(input$x,input$y,input$z)}))