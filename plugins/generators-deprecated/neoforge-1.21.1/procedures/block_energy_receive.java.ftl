<#include "mcelements.ftl">
<#-- @formatter:off -->
if (world instanceof ILevelExtension _ext) {
	IEnergyStorage _entityStorage = _ext.getCapability(Capabilities.EnergyStorage.BLOCK, ${toBlockPos(input$x,input$y,input$z)}, ${input$direction});
	if (_entityStorage != null)
		_entityStorage.receiveEnergy(${opt.toInt(input$amount)}, false);
}
<#-- @formatter:on -->