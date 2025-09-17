<#include "mcitems.ftl">
<@head>if (${input$entity} instanceof Player _player) {</@head>
	ItemStack _setstack${cbi} = ${mappedMCItemToItemStackCode(input$item, 1)}.copy();
	_setstack${cbi}.setCount(${opt.toInt(input$amount)});
	ItemHandlerHelper.giveItemToPlayer(_player, _setstack${cbi});
<@tail>}</@tail>