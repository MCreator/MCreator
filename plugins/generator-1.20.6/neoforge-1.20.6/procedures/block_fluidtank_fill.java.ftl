<#include "mcelements.ftl">
<#include "mcitems.ftl">
<#-- @formatter:off -->
if (world instanceof ILevelExtension _ext) {
	IFluidHandler _fluidHandler = _ext.getCapability(Capabilities.FluidHandler.BLOCK, ${toBlockPos(input$x,input$y,input$z)}, ${input$direction});
	if (_fluidHandler != null)
		<#if field$fluid.startsWith("CUSTOM:")>
		<#assign fluid = field$fluid?replace("CUSTOM:", "")>
		_fluidHandler.fill(new FluidStack(${JavaModName}Fluids.${fluid?ends_with(":Flowing")?then("FLOWING_","")}${generator.getRegistryNameForModElement(fluid?remove_ending(":Flowing"))?upper_case}.get(), ${opt.toInt(input$amount)}), IFluidHandler.FluidAction.EXECUTE);
		<#else>
		_fluidHandler.fill(new FluidStack(Fluids.${generator.map(field$fluid, "fluids")}, ${opt.toInt(input$amount)}), IFluidHandler.FluidAction.EXECUTE);
		</#if>
}
<#-- @formatter:on -->