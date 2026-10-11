<#include "mcitems.ftl">
<@addTemplate file="utils/item/itemhandler_set_slot.java.ftl"/>
ItemStack _itemStack${cbi} = ${mappedMCItemToItemStackCode(input$item, 1)};
if (_itemStack${cbi}.getCapability(Capabilities.Item.ITEM, ItemAccess.forStack(_itemStack${cbi})) instanceof ResourceHandler<ItemResource> _resourceHandler) {
	setStackInSlot(_resourceHandler, ${opt.toInt(input$slotid)}, ItemResource.of(${mappedMCItemToItemStackCode(input$slotitem, 1)}), ${opt.toInt(input$amount)});
}