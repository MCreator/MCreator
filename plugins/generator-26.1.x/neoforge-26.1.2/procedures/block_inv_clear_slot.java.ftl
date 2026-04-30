<#include "mcelements.ftl">
<#-- @formatter:off -->
if (world instanceof ServerLevel _serverLevel) {
	BlockEntity be = _serverLevel.getBlockEntity(${toBlockPos(input$x,input$y,input$z)});
	if (be instanceof Container container) {
		container.setItem(${opt.toInt(input$slotid)}, ItemStack.EMPTY);
	}
}
<#-- @formatter:on -->