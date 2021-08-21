<#include "mcitems.ftl">
if(${input$entity} instanceof PlayerEntity)
	((PlayerEntity)${input$entity}).getCooldownTracker().setCooldown(${mappedMCItemToItem(input$item)}, (int) ${input$ticks});