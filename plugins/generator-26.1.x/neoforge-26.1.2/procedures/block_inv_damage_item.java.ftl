<#include "mcelements.ftl">
<#-- @formatter:off -->
if (world instanceof ServerLevel _serverLevel) {
	BlockEntity be = _serverLevel.getBlockEntity(${toBlockPos(input$x,input$y,input$z)});
	if (be instanceof Container container) {
		container.getItem(${opt.toInt(input$slotid)}).hurtAndBreak(${opt.toInt(input$amount)}, _serverLevel, null, _stkprov -> {});
	}
}
<#-- @formatter:on -->