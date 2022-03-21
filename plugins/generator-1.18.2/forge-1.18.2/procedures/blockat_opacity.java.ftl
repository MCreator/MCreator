<#include "mcelements.ftl">
/*@int*/(world.getBlockState(${toBlockPos(input$x,input$y,input$z)}).getLightBlock(world,${toBlockPos(input$x,input$y,input$z)}))