<#include "mcelements.ftl">
<#-- @formatter:off -->
/*@int*/(new Object() {
	public int getBlockTanks(LevelAccessor level, BlockPos pos) {
		if (level instanceof ILevelExtension _ext) {
			IFluidHandler _fluidHandler = _ext.getCapability(Capabilities.FluidHandler.BLOCK, pos, ${input$direction});
			if (_fluidHandler != null)
				return _fluidHandler.getTanks();
		}
		return 0;
	}
}.getBlockTanks(world, ${toBlockPos(input$x,input$y,input$z)}))
<#-- @formatter:on -->