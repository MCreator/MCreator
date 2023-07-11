<#include "mcelements.ftl">
/*@float*/(world.getBlockState(${toBlockPos(input$x,input$y,input$z)}).getDestroySpeed(world, ${toBlockPos(input$x,input$y,input$z)}))