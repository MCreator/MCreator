<#include "mcitems.ftl">
<@head>if (${input$entity} instanceof Player _player) {</@head>
	ItemStack _stktoremove = ${mappedMCItemToItemStackCode(input$item, 1)};
	_player.getInventory()
		.clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), ${opt.toInt(input$amount)}, _player.inventoryMenu.getCraftSlots());
<@tail>}</@tail>