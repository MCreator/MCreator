<#include "mcitems.ftl">
if(world.isClientSide() && Minecraft.getInstance().player != null)
	Minecraft.getInstance().player.displayItemActivation(${mappedMCItemToItemStackCode(input$item, 1)});