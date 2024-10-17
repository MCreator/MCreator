<#include "mcelements.ftl">
<#-- @formatter:off -->
if (world instanceof ILevelExtension _ext && _ext.getCapability(Capabilities.ItemHandler.BLOCK, ${toBlockPos(input$x,input$y,input$z)}, null) instanceof IItemHandlerModifiable _itemHandlerModifiable) {
	int _slotid = ${opt.toInt(input$slotid)};
	ItemStack _stk = _itemHandlerModifiable.getStackInSlot(_slotid).copy();
	_stk.shrink(${opt.toInt(input$amount)});
	_itemHandlerModifiable.setStackInSlot(_slotid, _stk);
}
<#-- @formatter:on -->