<#include "mcitems.ftl">
<#-- @formatter:off -->
{
	BlockEntity _ent = world.getBlockEntity(new BlockPos((int) ${input$x}, (int) ${input$y}, (int) ${input$z}));
	if (_ent != null) {
		final int _slotid = ${opt.toInt(input$slotid)};
		final ItemStack _setstack = ${mappedMCItemToItemStackCode(input$item, 1)};
		_setstack.setCount(${opt.toInt(input$amount)});
		_ent.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(capability -> {
			if (capability instanceof IItemHandlerModifiable)
				((IItemHandlerModifiable) capability).setStackInSlot(_slotid, _setstack);
		});
	}
}
<#-- @formatter:on -->