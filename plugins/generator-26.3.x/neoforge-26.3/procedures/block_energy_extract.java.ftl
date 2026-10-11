<#include "mcelements.ftl">
<#-- @formatter:off -->
if (world instanceof ILevelExtension _ext) {
	if (_ext.getCapability(Capabilities.Energy.BLOCK, ${toBlockPos(input$x,input$y,input$z)}, ${input$direction}) instanceof EnergyHandler _energyHandler) {
		try (var _tx = Transaction.openRoot()) {
			_energyHandler.extract(${opt.toInt(input$amount)}, _tx);
            _tx.commit();
        }
	}
}
<#-- @formatter:on -->