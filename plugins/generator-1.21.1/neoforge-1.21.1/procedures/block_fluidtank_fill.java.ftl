<#include "mcelements.ftl">
<#include "mcitems.ftl">
<#-- @formatter:off -->
<@head>if (world instanceof ILevelExtension _ext) {</@head>
	IFluidHandler _fluidHandler${cbi} = _ext.getCapability(Capabilities.FluidHandler.BLOCK, ${toBlockPos(input$x,input$y,input$z)}, ${input$direction});
	if (_fluidHandler != null)
		_fluidHandler${cbi}.fill(new FluidStack(${generator.map(field$fluid, "fluids")}, ${opt.toInt(input$amount)}), IFluidHandler.FluidAction.EXECUTE);
<@tail>}</@tail>
<#-- @formatter:on -->