<#include "mcelements.ftl">
(world.getBlockState(${toBlockPos(input$x,input$y,input$z)}).getBlock() instanceof IPlantable _plant${customBlockIndex} &&
	_plant${customBlockIndex}.getPlantType(world, ${toBlockPos(input$x,input$y,input$z)}) == PlantType.${generator.map(field$planttype, "planttypes")})