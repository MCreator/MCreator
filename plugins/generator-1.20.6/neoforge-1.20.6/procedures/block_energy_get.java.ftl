<#include "mcelements.ftl">
<#-- @formatter:off -->
/*@int*/(new Object(){
	public int getEnergyStored(LevelAccessor level, BlockPos pos) {
		if (level instanceof ILevelExtension _ext) {
			IEnergyStorage _entityStorage = _ext.getCapability(Capabilities.EnergyStorage.BLOCK, pos, ${input$direction});
			if (_entityStorage != null)
				return _entityStorage.getEnergyStored();
		}
		return 0;
	}
}.getEnergyStored(world, ${toBlockPos(input$x,input$y,input$z)}))
<#-- @formatter:on -->