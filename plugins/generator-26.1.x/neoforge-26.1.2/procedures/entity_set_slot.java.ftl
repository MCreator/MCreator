<#include "mcitems.ftl">
<@addTemplate file="utils/item/itemhandler_set_slot.java.ftl"/>
ResourceHandler<ItemResource> _resourceHandler${cbi} = entity.getCapability(Capabilities.Item.ENTITY, null);
if (_resourceHandler${cbi} != null) {
	setStackInSlot(_resourceHandler${cbi}, ${opt.toInt(input$slotid)}, ItemResource.of(${mappedMCItemToItemStackCode(input$slotitem, 1)}), ${opt.toInt(input$amount)});
}