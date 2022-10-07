<#include "mcitems.ftl">
{
	ItemStack _isc = ${mappedMCItemToItemStackCode(input$item, 1)};
	final ItemStack _setstack = ${mappedMCItemToItemStackCode(input$slotitem, 1)};
	final int _sltid = ${opt.toInt(input$slotid)};
	_setstack.setCount(${opt.toInt(input$amount)});
	_isc.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
		if (capability instanceof IItemHandlerModifiable) {
            ((IItemHandlerModifiable) capability).setStackInSlot(_sltid, _setstack);
        }
	});
}