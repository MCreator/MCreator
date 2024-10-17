<#include "mcelements.ftl">
(world.getBlockState(${toBlockPos(input$x,input$y,input$z)}).isFaceSturdy(world, ${toBlockPos(input$x,input$y,input$z)}, ${input$direction}))