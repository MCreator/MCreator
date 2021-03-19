<#include "mcitems.ftl">
{
	ItemStack _isc = ${mappedMCItemToItemStackCode(input$item, 1)};
	final ItemStack _setstack = ${mappedMCItemToItemStackCode(input$slotitem, 1)};
	final int _sltid = (int)(${input$slotid});
	_setstack.setCount((int) ${input$amount});
	_isc.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(capability -> {
		if (capability instanceof IItemHandlerModifiable) {
            ((IItemHandlerModifiable) capability).setStackInSlot(_sltid, _setstack);
        }
	});
}