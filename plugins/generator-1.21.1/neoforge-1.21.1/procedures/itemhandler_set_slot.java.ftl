<#include "mcitems.ftl">
if (${mappedMCItemToItemStackCode(input$item, 1)}.getCapability(Capabilities.ItemHandler.ITEM, null) instanceof IItemHandlerModifiable _modHandlerItemSetSlot) {
	ItemStack _setstack = ${mappedMCItemToItemStackCode(input$slotitem, 1)}.copy();
	_setstack.setCount(${opt.toInt(input$amount)});
	_modHandlerItemSetSlot.setStackInSlot(${opt.toInt(input$slotid)}, _setstack);
}