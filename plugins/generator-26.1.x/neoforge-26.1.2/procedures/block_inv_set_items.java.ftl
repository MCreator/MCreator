<#include "mcelements.ftl">
<#include "mcitems.ftl">
<#-- @formatter:off -->
if (world instanceof ServerLevel _serverLevel) {
	BlockEntity be = _serverLevel.getBlockEntity(${toBlockPos(input$x,input$y,input$z)});
	if (be instanceof Container container) {
		ItemStack _setstack = ${mappedMCItemToItemStackCode(input$item, 1)}.copy();
        _setstack.setCount(${opt.toInt(input$amount)});
		container.setItem(${opt.toInt(input$slotid)}, _setstack);
	}
}
<#-- @formatter:on -->