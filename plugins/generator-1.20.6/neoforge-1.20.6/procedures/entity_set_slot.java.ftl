<#include "mcitems.ftl">
if (${input$entity}.getCapability(Capabilities.ItemHandler.ENTITY, null) instanceof IItemHandlerModifiable _modHandler) {
	ItemStack _setstack = ${mappedMCItemToItemStackCode(input$slotitem, 1)}.copy();
	_setstack.setCount(${opt.toInt(input$amount)});
	_modHandler.setStackInSlot(${opt.toInt(input$slotid)}, _setstack);
}