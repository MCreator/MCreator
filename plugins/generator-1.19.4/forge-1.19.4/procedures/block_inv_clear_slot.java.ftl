<#include "mcelements.ftl">
<#-- @formatter:off -->
{
	BlockEntity _ent = world.getBlockEntity(${toBlockPos(input$x,input$y,input$z)});
	if (_ent != null) {
		final int _slotid = ${opt.toInt(input$slotid)};
		_ent.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
			if (capability instanceof IItemHandlerModifiable)
				((IItemHandlerModifiable) capability).setStackInSlot(_slotid, ItemStack.EMPTY);
		});
	}
}
<#-- @formatter:on -->