<#include "mcelements.ftl">
(world.getBlockState(${toBlockPos(input$x,input$y,input$z)}).getBlock() instanceof IPlantable _plant ?
	_plant.getPlantType(world, ${toBlockPos(input$x,input$y,input$z)}) == PlantType.${generator.map(field$planttype, "planttypes")} : false)