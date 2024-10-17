<#include "mcelements.ftl">
world.getPendingBlockTicks().scheduleTick(${toBlockPos(input$x,input$y,input$z)},
		world.getBlockState(${toBlockPos(input$x,input$y,input$z)}).getBlock(), (int)${input$ticks});