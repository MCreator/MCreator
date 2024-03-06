<#include "mcitems.ftl">
{
	final int _slotid = ${opt.toInt(input$slotid)};
	final ItemStack _setstack = ${mappedMCItemToItemStackCode(input$slotitem, 1)}.copy();
	_setstack.setCount(${opt.toInt(input$amount)});
	${input$entity}.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
		if (capability instanceof IItemHandlerModifiable _modHandler)
			_modHandler.setStackInSlot(_slotid, _setstack);
	});
}