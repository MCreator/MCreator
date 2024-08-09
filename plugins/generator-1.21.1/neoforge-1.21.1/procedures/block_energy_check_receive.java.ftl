<#include "mcelements.ftl">
<#-- @formatter:off -->
/*@int*/(new Object(){
	public int receiveEnergySimulate(LevelAccessor level, BlockPos pos, int _amount) {
		if (level instanceof ILevelExtension _ext) {
			IEnergyStorage _entityStorage = _ext.getCapability(Capabilities.EnergyStorage.BLOCK, pos, ${input$direction});
			if (_entityStorage != null)
				return _entityStorage.receiveEnergy(_amount, true);
		}
		return 0;
	}
}.receiveEnergySimulate(world, ${toBlockPos(input$x,input$y,input$z)},${opt.toInt(input$amount)}))
<#-- @formatter:on -->