<#include "mcelements.ftl">
<#include "mcitems.ftl">
<#-- @formatter:off -->
if (world instanceof ILevelExtension _ext) {
	if (_ext.getCapability(Capabilities.Fluid.BLOCK, ${toBlockPos(input$x,input$y,input$z)}, ${input$direction}) instanceof ResourceHandler<FluidResource> _fluidHandler) {
		int _fillAmount = ${opt.toInt(input$amount)};
		if (_fillAmount > 0) {
			try (var _tx = Transaction.openRoot()) {
				_fluidHandler.insert(FluidResource.of(${generator.map(field$fluid, "fluids")}), _fillAmount, _tx);
				_tx.commit();
			}
		}
	}
}
<#-- @formatter:on -->