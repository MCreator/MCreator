<#include "mcitems.ftl">
<#-- @formatter:off -->
{
	TileEntity _ent = world.getTileEntity(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}));
	int _amount = (int)${input$amount};
	if (_ent != null)
		_ent.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, ${input$direction}).ifPresent(capability ->
			capability.drain(_amount, IFluidHandler.FluidAction.EXECUTE));
}
<#-- @formatter:on -->