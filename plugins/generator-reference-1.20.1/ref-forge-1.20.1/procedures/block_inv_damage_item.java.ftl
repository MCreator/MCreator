<#include "mcelements.ftl">
<#-- @formatter:off -->
{
	BlockEntity _ent = world.getBlockEntity(${toBlockPos(input$x,input$y,input$z)});
	if (_ent != null) {
		final int _slotid = ${opt.toInt(input$slotid)};
		final int _amount = ${opt.toInt(input$amount)};
		_ent.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
			if (capability instanceof IItemHandlerModifiable) {
				ItemStack _stk = capability.getStackInSlot(_slotid).copy();
				if (_stk.hurt(_amount, RandomSource.create(), null)) {
					_stk.shrink(1);
					_stk.setDamageValue(0);
				}
				((IItemHandlerModifiable) capability).setStackInSlot(_slotid, _stk);
			}
		});
	}
}
<#-- @formatter:on -->