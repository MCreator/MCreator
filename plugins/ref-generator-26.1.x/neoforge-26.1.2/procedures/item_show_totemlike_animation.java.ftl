<#include "mcitems.ftl">
if(world.isClientSide())
	Minecraft.getInstance().gameRenderer.displayItemActivation(${mappedMCItemToItemStackCode(input$item, 1)});