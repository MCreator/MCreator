<#include "mcelements.ftl">
<#-- @formatter:off -->
if (world instanceof ILevelExtension _ext && _ext.getCapability(Capabilities.ItemHandler.BLOCK, ${toBlockPos(input$x,input$y,input$z)}, null) instanceof IItemHandlerModifiable _itemHandlerModifiable)
	_itemHandlerModifiable.setStackInSlot(${opt.toInt(input$slotid)}, ItemStack.EMPTY);
<#-- @formatter:on -->