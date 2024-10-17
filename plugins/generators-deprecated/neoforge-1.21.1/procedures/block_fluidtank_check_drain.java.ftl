<#include "mcelements.ftl">
<#-- @formatter:off -->
/*@int*/(new Object(){
	public int drainTankSimulate(LevelAccessor level, BlockPos pos, int amount) {
		if (level instanceof ILevelExtension _ext) {
			IFluidHandler _fluidHandler = _ext.getCapability(Capabilities.FluidHandler.BLOCK, pos, ${input$direction});
			if (_fluidHandler != null)
				return _fluidHandler.drain(amount, IFluidHandler.FluidAction.SIMULATE).getAmount();
		}
		return 0;
	}
}.drainTankSimulate(world, ${toBlockPos(input$x,input$y,input$z)},${opt.toInt(input$amount)}))
<#-- @formatter:on -->