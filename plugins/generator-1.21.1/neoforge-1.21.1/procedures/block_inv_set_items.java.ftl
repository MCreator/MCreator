<#include "mcelements.ftl">
<#include "mcitems.ftl">
<#-- @formatter:off -->
<@head>if (world instanceof ILevelExtension _ext) {</@head>
	if (_ext.getCapability(Capabilities.ItemHandler.BLOCK, ${toBlockPos(input$x,input$y,input$z)}, null) instanceof IItemHandlerModifiable _itemHandlerModifiable) {
		ItemStack _setstack = ${mappedMCItemToItemStackCode(input$item, 1)}.copy();
		_setstack.setCount(${opt.toInt(input$amount)});
		_itemHandlerModifiable.setStackInSlot(${opt.toInt(input$slotid)}, _setstack);
	}
<@tail>}</@tai>
<#-- @formatter:on -->