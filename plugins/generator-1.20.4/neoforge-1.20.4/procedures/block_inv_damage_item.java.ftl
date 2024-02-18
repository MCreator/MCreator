<#include "mcelements.ftl">
<#-- @formatter:off -->
if (world instanceof ILevelExtension _ext && _ext.getCapability(Capabilities.ItemHandler.BLOCK, ${toBlockPos(input$x,input$y,input$z)}, null) instanceof IItemHandlerModifiable _itemHandlerModifiable) {
	int _slotid = ${opt.toInt(input$slotid)};
	ItemStack _stk = _itemHandlerModifiable.getStackInSlot(_slotid).copy();
	if (_stk.hurt(${opt.toInt(input$amount)}, RandomSource.create(), null)) {
		_stk.shrink(1);
		_stk.setDamageValue(0);
	}
	_itemHandlerModifiable.setStackInSlot(_slotid, _stk);
}
<#-- @formatter:on -->