<#include "mcelements.ftl">
<#-- @formatter:off -->
/*@int*/(new Object(){
	public int getMaxEnergyStored(LevelAccessor level, BlockPos pos) {
		if (level instanceof ILevelExtension _ext) {
			IEnergyStorage _entityStorage = _ext.getCapability(Capabilities.EnergyStorage.BLOCK, pos, ${input$direction});
			if (_entityStorage != null)
				return _entityStorage.getMaxEnergyStored();
		}
		return 0;
	}
}.getMaxEnergyStored(world, ${toBlockPos(input$x,input$y,input$z)}))
<#-- @formatter:on -->