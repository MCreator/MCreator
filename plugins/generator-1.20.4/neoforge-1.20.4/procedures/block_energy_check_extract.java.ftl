<#include "mcelements.ftl">
<#-- @formatter:off -->
/*@int*/(new Object(){
	public int extractEnergySimulate(LevelAccessor level, BlockPos pos, int _amount) {
		if (level instanceof ILevelExtension _ext) {
			IEnergyStorage _entityStorage = _ext.getCapability(Capabilities.EnergyStorage.BLOCK, pos, ${input$direction});
			if (_entityStorage != null)
				return _entityStorage.extractEnergy(_amount, true);
		}
		return 0;
	}
}.extractEnergySimulate(world, ${toBlockPos(input$x,input$y,input$z)},${opt.toInt(input$amount)}))
<#-- @formatter:on -->