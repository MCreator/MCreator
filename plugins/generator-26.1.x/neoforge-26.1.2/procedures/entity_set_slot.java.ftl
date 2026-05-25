<#include "mcitems.ftl">
<@addTemplate file="utils/item/itemhandler_set_slot.java.ftl"/>
if (entity.getCapability(Capabilities.Item.ENTITY, null) instanceof ResourceHandler<ItemResource> _resourceHandler) {
	setStackInSlot(_resourceHandler, ${opt.toInt(input$slotid)}, ItemResource.of(${mappedMCItemToItemStackCode(input$slotitem, 1)}), ${opt.toInt(input$amount)});
}