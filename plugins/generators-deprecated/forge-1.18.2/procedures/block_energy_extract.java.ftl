<#include "mcelements.ftl">
<#-- @formatter:off -->
{
	BlockEntity _ent = world.getBlockEntity(${toBlockPos(input$x,input$y,input$z)});
	int _amount = ${opt.toInt(input$amount)};
	if (_ent != null)
		_ent.getCapability(CapabilityEnergy.ENERGY, ${input$direction}).ifPresent(capability ->
			capability.extractEnergy(_amount, false));
}
<#-- @formatter:on -->