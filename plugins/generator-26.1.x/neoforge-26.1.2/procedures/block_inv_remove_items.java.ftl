<#include "mcelements.ftl">
<@addTemplate file="utils/item/itemhandler_set_slot.java.ftl"/>
<#-- @formatter:off -->
if (world instanceof ILevelExtension _ext && _ext.getCapability(Capabilities.Item.BLOCK, ${toBlockPos(input$x,input$y,input$z)}, null) instanceof ResourceHandler<ItemResource> _resourceHandler) {
	int _slotid = ${opt.toInt(input$slotid)};
	setStackInSlot(_resourceHandler, _slotid, _resourceHandler.getResource(_slotid), _resourceHandler.getAmountAsInt(_slotid) - ${opt.toInt(input$amount)});
}
<#-- @formatter:on -->