<#include "mcelements.ftl">
<#-- @formatter:off -->
if (world instanceof ServerLevel _serverLevel) {
	BlockEntity _be = _serverLevel.getBlockEntity(${toBlockPos(input$x,input$y,input$z)});
	if (_be instanceof Container _container) {
		_container.getItem(${opt.toInt(input$slotid)}).shrink(${opt.toInt(input$amount)});
	}
}
<#-- @formatter:on -->