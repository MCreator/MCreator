<#include "mcelements.ftl">
(world.getBiome(${toBlockPos(input$x,input$y,input$z)}).getTemperature(${toBlockPos(input$x,input$y,input$z)})*100.f)