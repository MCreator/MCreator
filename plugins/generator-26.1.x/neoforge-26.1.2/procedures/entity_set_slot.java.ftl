<#include "mcitems.ftl">
if (entity.getCapability(Capabilities.Item.ENTITY, null) instanceof ResourceHandler<ItemResource> _resourceHandler) {
	ItemStack _setstack = ${mappedMCItemToItemStackCode(input$slotitem, 1)}.copy();
	_setstack.setCount(${opt.toInt(input$amount)});
	ItemUtil.insertItemReturnRemaining(_resourceHandler, ${opt.toInt(input$slotid)}, _setstack, false, null);
}