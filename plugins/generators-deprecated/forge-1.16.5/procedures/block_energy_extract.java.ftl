<#include "mcelements.ftl">
<#-- @formatter:off -->
{
	TileEntity _ent = world.getTileEntity(${toBlockPos(input$x,input$y,input$z)});
	int _amount = (int)${input$amount};
	if (_ent != null)
		_ent.getCapability(CapabilityEnergy.ENERGY, ${input$direction}).ifPresent(capability ->
			capability.extractEnergy(_amount, false));
}
<#-- @formatter:on -->