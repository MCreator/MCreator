<#include "mcelements.ftl">
<#-- @formatter:off -->
(new Object(){
	public boolean canExtractEnergy(LevelAccessor level, BlockPos pos) {
		if (level instanceof ILevelExtension _ext) {
			IEnergyStorage _entityStorage = _ext.getCapability(Capabilities.EnergyStorage.BLOCK, pos, ${input$direction});
			if (_entityStorage != null)
				return _entityStorage.canExtract();
		}
		return false;
	}
}.canExtractEnergy(world, ${toBlockPos(input$x,input$y,input$z)}))
<#-- @formatter:on -->