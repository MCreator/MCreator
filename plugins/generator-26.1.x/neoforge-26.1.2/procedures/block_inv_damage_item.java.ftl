<#include "mcelements.ftl">
<@addTemplate file="utils/item/itemhandler_set_slot.java.ftl"/>
<#-- @formatter:off -->
if (world instanceof ILevelExtension _ext && world instanceof ServerLevel _serverLevel &&
	_ext.getCapability(Capabilities.Item.BLOCK, ${toBlockPos(input$x,input$y,input$z)}, null) instanceof ResourceHandler<ItemResource> _resourceHandler) {
	int _slotid = ${opt.toInt(input$slotid)};
	ItemStack _stk = ItemUtil.getStack(_resourceHandler, _slotid);
	_stk.hurtAndBreak(${opt.toInt(input$amount)}, _serverLevel, null, _stkprov -> {});
	setStackInSlot(_resourceHandler, _slotid, ItemResource.of(_stk), _stk.getCount());
}
<#-- @formatter:on -->