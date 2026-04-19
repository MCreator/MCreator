<#include "mcitems.ftl">
<@head>if (${input$entity} instanceof Player _player && _player.containerMenu instanceof ${JavaModName}Menus.MenuAccessor _menu) {</@head>
	ItemStack _setstack${cbi} = ${mappedMCItemToItemStackCode(input$item, 1)}.copy();
	_setstack${cbi}.setCount(${opt.toInt(input$amount)});
	_menu.getSlots().get(${opt.toInt(input$slotid)}).set(_setstack${cbi});
<@tail>
	_player.containerMenu.broadcastChanges();
}</@tail>