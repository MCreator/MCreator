<#include "mcelements.ftl">
<#include "mcitems.ftl">
<@addTemplate file="utils/item/itemhandler_set_slot.java.ftl"/>
<#-- @formatter:off -->
if (world instanceof ILevelExtension _ext && _ext.getCapability(Capabilities.Item.BLOCK, ${toBlockPos(input$x,input$y,input$z)}, null) instanceof ResourceHandler<ItemResource> _resourceHandler) {
	setStackInSlot(_resourceHandler, ${opt.toInt(input$slotid)}, ItemResource.of(${mappedMCItemToItemStackCode(input$item, 1)}), ${opt.toInt(input$amount)});  
}
<#-- @formatter:on -->