<#include "mcelements.ftl">
if(world.getWorldInfo() instanceof ISpawnWorldInfo)
    ((ISpawnWorldInfo) world.getWorldInfo()).setSpawn(${toBlockPos(input$x,input$y,input$z)}, 0);