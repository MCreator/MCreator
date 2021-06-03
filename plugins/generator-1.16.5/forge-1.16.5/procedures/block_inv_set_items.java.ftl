<#include "mcitems.ftl">
<#-- @formatter:off -->
{
	TileEntity _ent = world.getTileEntity(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}));
	if (_ent != null) {
		final int _sltid = (int)(${input$slotid});
		final ItemStack _setstack = ${mappedMCItemToItemStackCode(input$item, 1)};
		_setstack.setCount((int) ${input$amount});
		_ent.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(capability -> {
			if (capability instanceof IItemHandlerModifiable) {
				((IItemHandlerModifiable) capability).setStackInSlot(_sltid, _setstack);
			}
		});
	}
}
<#-- @formatter:on -->