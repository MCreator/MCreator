<#include "mcitems.ftl">
{
	final int _slotid = ${opt.toInt(input$slotid)};
	final ItemStack _setstack = ${mappedMCItemToItemStackCode(input$slotitem, 1)};
	_setstack.setCount(${opt.toInt(input$amount)});
	${input$entity}.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(capability -> {
		if (capability instanceof IItemHandlerModifiable _modHandler)
            _modHandler.setStackInSlot(_slotid, _setstack);
	});
}