<#include "mcitems.ftl">
if (${input$entity} instanceof LivingEntity _entity) {
	ItemStack _setstack = ${mappedMCItemToItemStackCode(input$item, 1)}.copy();
	_setstack.setCount(${opt.toInt(input$amount)});
	_entity.setItemInHand(InteractionHand.OFF_HAND, _setstack);
	if (_entity instanceof Player _player) _player.getInventory().setChanged();
}