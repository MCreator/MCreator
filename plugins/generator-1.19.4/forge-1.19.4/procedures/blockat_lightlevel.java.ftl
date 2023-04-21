<#include "mcelements.ftl">
/*@int*/(world.getBlockState(${toBlockPos(input$x,input$y,input$z)}).getLightEmission(world, ${toBlockPos(input$x,input$y,input$z)}))