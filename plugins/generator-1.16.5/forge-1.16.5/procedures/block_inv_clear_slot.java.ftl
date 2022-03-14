<#include "mcelements.ftl">
<#-- @formatter:off -->
{
	TileEntity _ent = world.getTileEntity(${toBlockPos(input$x,input$y,input$z)});
	if (_ent != null) {
		final int _sltid = (int)(${input$slotid});
		_ent.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(capability -> {
			if (capability instanceof IItemHandlerModifiable) {
				((IItemHandlerModifiable) capability).setStackInSlot(_sltid, ItemStack.EMPTY);
			}
		});
	}
}
<#-- @formatter:on -->