<#include "mcelements.ftl">
/*@float*/(world.getBiome(${toBlockPos(input$x,input$y,input$z)}).value().getBaseTemperature() * 100f)