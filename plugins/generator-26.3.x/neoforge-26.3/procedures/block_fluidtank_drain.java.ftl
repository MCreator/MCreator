<#include "mcelements.ftl">
<#include "mcitems.ftl">
<#-- @formatter:off -->
if (world instanceof ILevelExtension _ext) {
	if (_ext.getCapability(Capabilities.Fluid.BLOCK, ${toBlockPos(input$x,input$y,input$z)}, ${input$direction}) instanceof ResourceHandler<FluidResource> _fluidHandler) {
		int _drainAmount = ${opt.toInt(input$amount)};
		if (_drainAmount > 0) {
			try (var _tx = Transaction.openRoot()) {
				ResourceHandlerUtil.extractFirst(_fluidHandler, _ -> true, _drainAmount, _tx);
				_tx.commit();
			}
		}
	}
}
<#-- @formatter:on -->