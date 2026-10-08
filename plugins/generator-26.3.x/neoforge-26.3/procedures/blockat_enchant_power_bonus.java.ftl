<#include "mcelements.ftl">
/*@float*/(world.getBlockState(${toBlockPos(input$x,input$y,input$z)}).getEnchantPowerBonus(world, ${toBlockPos(input$x,input$y,input$z)}))