<#include "mcitems.ftl">
<@head>if (${input$entity} instanceof Player _player) {
  	if (_player.containerMenu instanceof ${JavaModName}Menus.MenuAccessor _menu) {</@head>
		ItemStack _setstack$ = ${mappedMCItemToItemStackCode(input$item, 1)}.copy();
		_setstack.setCount(${opt.toInt(input$amount)});
		_menu.getSlots().get(${opt.toInt(input$slotid)}).set(_setstack);
<@tail>}
	_player.containerMenu.broadcastChanges();
}</@tail>