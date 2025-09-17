<#include "mcelements.ftl">
<#-- @formatter:off -->
<@head>if (world instanceof ILevelExtension _ext) {</@head>
	IEnergyStorage _entityStorage${cbi} = _ext.getCapability(Capabilities.EnergyStorage.BLOCK, ${toBlockPos(input$x,input$y,input$z)}, ${input$direction});
	if (_entityStorage != null)
		_entityStorage${cbi}.extractEnergy(${opt.toInt(input$amount)}, false);
<@tail>}</@tail>
<#-- @formatter:on -->