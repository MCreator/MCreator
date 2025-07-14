<#include "mcitems.ftl">
if(${input$entity} instanceof Player _player && _player.containerMenu instanceof ${JavaModName}Menus.MenuAccessor _menu) {
	ItemStack _setstack = ${mappedMCItemToItemStackCode(input$item, 1)}.copy();
	_setstack.setCount(${opt.toInt(input$amount)});
	_menu.getSlots().get(${opt.toInt(input$slotid)}).set(_setstack);
	_player.containerMenu.broadcastChanges();
}