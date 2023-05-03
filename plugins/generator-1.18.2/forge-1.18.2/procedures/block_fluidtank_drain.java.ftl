<#include "mcelements.ftl">
<#include "mcitems.ftl">
<#-- @formatter:off -->
{
	BlockEntity _ent = world.getBlockEntity(${toBlockPos(input$x,input$y,input$z)});
	int _amount = ${opt.toInt(input$amount)};
	if (_ent != null)
		_ent.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, ${input$direction}).ifPresent(capability ->
			capability.drain(_amount, IFluidHandler.FluidAction.EXECUTE));
}
<#-- @formatter:on -->