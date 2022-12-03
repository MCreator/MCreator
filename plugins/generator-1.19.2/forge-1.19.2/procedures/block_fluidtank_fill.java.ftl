<#include "mcelements.ftl">
<#include "mcitems.ftl">
<#-- @formatter:off -->
{
	BlockEntity _ent = world.getBlockEntity(${toBlockPos(input$x,input$y,input$z)});
	int _amount = ${opt.toInt(input$amount)};
	if (_ent != null)
		_ent.getCapability(ForgeCapabilities.FLUID_HANDLER, ${input$direction}).ifPresent(capability ->
			<#if field$fluid.startsWith("CUSTOM:")>
			<#assign fluid = field$fluid?replace("CUSTOM:", "")>
			capability.fill(new FluidStack(${JavaModName}Fluids.${fluid?ends_with(":Flowing")?then("FLOWING_","")}${generator.getRegistryNameForModElement(fluid?remove_ending(":Flowing"))?upper_case}.get(), _amount), IFluidHandler.FluidAction.EXECUTE)
			<#else>
			capability.fill(new FluidStack(Fluids.${generator.map(field$fluid, "fluids")}, _amount), IFluidHandler.FluidAction.EXECUTE)
			</#if>
		);
}
<#-- @formatter:on -->