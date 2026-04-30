<#include "mcelements.ftl">
<#-- @formatter:off -->
if (world instanceof ServerLevel _serverLevel) {
	BlockEntity _be = _serverLevel.getBlockEntity(${toBlockPos(input$x,input$y,input$z)});
	if (_be instanceof Container container) {
		container.setItem(${opt.toInt(input$slotid)}, ItemStack.EMPTY);
	}
}
<#-- @formatter:on -->