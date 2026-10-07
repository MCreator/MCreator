<#include "mcitems.ftl">
if(${input$entity} instanceof Player _player)
	_player.getCooldowns().addCooldown(${mappedMCItemToItemStackCode(input$item)}, ${opt.toInt(input$ticks)});