<#include "mcelements.ftl">
<#-- @formatter:off -->
/*@int*/(new Object() {
	public int getFluidTankCapacity(LevelAccessor level, BlockPos pos, int tank) {
		if (level instanceof ILevelExtension _ext) {
			IFluidHandler _fluidHandler = _ext.getCapability(Capabilities.FluidHandler.BLOCK, pos, ${input$direction});
			if (_fluidHandler != null)
				return _fluidHandler.getTankCapacity(tank);
		}
		return 0;
	}
}.getFluidTankCapacity(world, ${toBlockPos(input$x,input$y,input$z)},${opt.toInt(input$tank)}))
<#-- @formatter:on -->