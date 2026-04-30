<#include "mcelements.ftl">
<#-- @formatter:off -->
if (world instanceof ServerLevel _serverLevel) {
	BlockEntity _be = _serverLevel.getBlockEntity(${toBlockPos(input$x,input$y,input$z)});
	if (_be instanceof Container container) {
		container.getItem(${opt.toInt(input$slotid)}).hurtAndBreak(${opt.toInt(input$amount)}, _serverLevel, null, _stkprov -> {});
	}
}
<#-- @formatter:on -->