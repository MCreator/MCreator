<#include "mcelements.ftl">
<#include "mcitems.ftl">
<#-- @formatter:off -->
{
	BlockEntity _ent = world.getBlockEntity(${toBlockPos(input$x,input$y,input$z)});
	int _amount = ${opt.toInt(input$amount)};
	if (_ent != null)
		_ent.getCapability(ForgeCapabilities.FLUID_HANDLER, ${input$direction}).ifPresent(capability ->
			capability.fill(new FluidStack(${generator.map(field$fluid, "fluids")}, _amount), IFluidHandler.FluidAction.EXECUTE)
		);
}
<#-- @formatter:on -->