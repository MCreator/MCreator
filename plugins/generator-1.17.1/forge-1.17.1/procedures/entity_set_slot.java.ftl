<#include "mcitems.ftl">
{
	final ItemStack _setstack = ${mappedMCItemToItemStackCode(input$slotitem, 1)};
	final int _sltid = ${opt.toInt(input$slotid)};
	_setstack.setCount(${opt.toInt(input$amount)});
	${input$entity}.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(capability -> {
		if (capability instanceof IItemHandlerModifiable _modHandler)
            _modHandler.setStackInSlot(_sltid, _setstack);
	});
}