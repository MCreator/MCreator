<#include "mcelements.ftl">
(world.getBlockState(${toBlockPos(input$x,input$y,input$z)}).isSolidSide(world, ${toBlockPos(input$x,input$y,input$z)}, ${input$direction}))