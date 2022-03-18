<#include "mcelements.ftl">
<#-- @formatter:off -->
{
	TileEntity _ent = world.getTileEntity(${toBlockPos(input$x,input$y,input$z)});
	if (_ent != null) {
		final int _sltid = (int)(${input$slotid});
		final int _amount = (int) ${input$amount};
		_ent.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(capability -> {
			if (capability instanceof IItemHandlerModifiable) {
				ItemStack _stk = capability.getStackInSlot(_sltid).copy();
				_stk.shrink(_amount);
				((IItemHandlerModifiable) capability).setStackInSlot(_sltid, _stk);
			}
		});
	}
}
<#-- @formatter:on -->