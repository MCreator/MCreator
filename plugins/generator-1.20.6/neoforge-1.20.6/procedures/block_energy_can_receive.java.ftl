<#include "mcelements.ftl">
<#-- @formatter:off -->
(new Object(){
	public boolean canReceiveEnergy(LevelAccessor level, BlockPos pos) {
		if (level instanceof ILevelExtension _ext) {
			IEnergyStorage _entityStorage = _ext.getCapability(Capabilities.EnergyStorage.BLOCK, pos, ${input$direction});
			if (_entityStorage != null)
				return _entityStorage.canReceive();
		}
		return false;
	}
}.canReceiveEnergy(world, ${toBlockPos(input$x,input$y,input$z)}))
<#-- @formatter:on -->