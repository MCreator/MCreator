<#include "mcitems.ftl">
if(${input$entity} instanceof Player _player)
	_player.getCooldowns().addCooldown(${mappedMCItemToItem(input$item)}, (int) ${input$ticks});