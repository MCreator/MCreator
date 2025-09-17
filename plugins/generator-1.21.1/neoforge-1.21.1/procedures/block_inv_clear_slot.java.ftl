<#include "mcelements.ftl">
<#-- @formatter:off -->
<@head>if (world instanceof ILevelExtension _ext && _ext.getCapability(Capabilities.ItemHandler.BLOCK, ${toBlockPos(input$x,input$y,input$z)}, null) instanceof IItemHandlerModifiable _itemHandlerModifiable){</@head>
	_itemHandlerModifiable.setStackInSlot(${opt.toInt(input$slotid)}, ItemStack.EMPTY);
<@tail>}</@tail>
<#-- @formatter:on -->