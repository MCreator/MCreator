<#include "mcitems.ftl">
ResourceHandler<ItemResource> _resourceHandler${cbi} = entity.getCapability(Capabilities.Item.ENTITY, null);
if (_resourceHandler${cbi} != null) {
	ItemStack _setstack = ${mappedMCItemToItemStackCode(input$slotitem, 1)}.copy();
	_setstack.setCount(${opt.toInt(input$amount)});
	ItemUtil.insertItemReturnRemaining(_resourceHandler${cbi}, ${opt.toInt(input$slotid)}, _setstack, false, null);
}