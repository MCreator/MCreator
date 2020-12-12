<#include "mcitems.ftl">
if(${input$entity} instanceof PlayerEntity)
	((PlayerEntity)${input$entity}).getCooldownTracker().setCooldown((${mappedMCItemToItemStackCode(input$item, 1)}).getItem(), (int) ${input$ticks});