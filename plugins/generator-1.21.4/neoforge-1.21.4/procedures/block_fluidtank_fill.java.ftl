<#include "mcelements.ftl">
<#include "mcitems.ftl">
<#-- @formatter:off -->
if (world instanceof ILevelExtension _ext) {
	IFluidHandler _fluidHandler = _ext.getCapability(Capabilities.FluidHandler.BLOCK, ${toBlockPos(input$x,input$y,input$z)}, ${input$direction});
	if (_fluidHandler != null)
		_fluidHandler.fill(new FluidStack(${generator.map(field$fluid, "fluids")}, ${opt.toInt(input$amount)}), IFluidHandler.FluidAction.EXECUTE);
}
<#-- @formatter:on -->