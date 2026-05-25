<#include "mcitems.ftl">
<@head>if (${input$entity} instanceof LivingEntity _entity) {</@head>
	ItemStack _setstack${cbi} = ${mappedMCItemToItemStackCode(input$item, 1)}.copy();
	_setstack${cbi}.setCount(${opt.toInt(input$amount)});
	_entity.setItemInHand(InteractionHand.MAIN_HAND, _setstack${cbi});
<@tail>
	if (_entity instanceof Player _player) _player.getInventory().setChanged();
}</@tail>