<#include "mcitems.ftl">
(${input$entity} instanceof Player _playerHasItem? _playerHasItem.getInventory()
	.contains(stack -> !stack.isEmpty() && ItemStack.isSameItem(stack, ${mappedMCItemToItemStackCode(input$item, 1)})) : false)