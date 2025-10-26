<#include "mcitems.ftl">
if (${input$entity} instanceof Player _player) {
	ItemStack _setstack = ${mappedMCItemToItemStackCode(input$item, 1)}.copy();
	_setstack.setCount(${opt.toInt(input$amount)});
	ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
}