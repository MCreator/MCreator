<#include "mcelements.ftl">
<@addTemplate file="utils/item/itemhandler_set_slot.java.ftl"/>
<#-- @formatter:off -->
if (world instanceof ILevelExtension _ext && _ext.getCapability(Capabilities.Item.BLOCK, ${toBlockPos(input$x,input$y,input$z)}, null) instanceof ResourceHandler<ItemResource> _resourceHandler) {
	int _slotid = ${opt.toInt(input$slotid)};
	ItemStack _stk = _resourceHandler.getResource(_slotid).toStack(_resourceHandler.getAmountAsInt(_slotid));
	setStackInSlot(_resourceHandler, _slotid, ItemResource.of(_stk), _stk.getCount() - ${opt.toInt(input$amount)});
}
<#-- @formatter:on -->