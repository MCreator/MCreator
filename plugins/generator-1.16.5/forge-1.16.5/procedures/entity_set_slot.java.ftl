<#include "mcitems.ftl">
{
	final ItemStack _setstack = ${mappedMCItemToItemStackCode(input$slotitem, 1)};
	final int _sltid = (int)(${input$slotid});
	_setstack.setCount((int) ${input$amount});
	${input$entity}.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(capability -> {
		if (capability instanceof IItemHandlerModifiable) {
            ((IItemHandlerModifiable) capability).setStackInSlot(_sltid, _setstack);
        }
	});
}