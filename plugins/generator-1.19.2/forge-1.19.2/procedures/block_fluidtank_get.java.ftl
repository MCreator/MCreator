<#include "mcelements.ftl">
<#-- @formatter:off -->
/*@int*/(new Object() {
	public int getFluidTankLevel(LevelAccessor _level, BlockPos _pos, int _tank) {
		AtomicInteger _retval = new AtomicInteger(0);
		BlockEntity _ent = _level.getBlockEntity(_pos);
		if (_ent != null)
			_ent.getCapability(ForgeCapabilities.FLUID_HANDLER, ${input$direction}).ifPresent(_capability ->
				_retval.set(_capability.getFluidInTank(_tank).getAmount()));
		return _retval.get();
	}
}.getFluidTankLevel(world, ${toBlockPos(input$x,input$y,input$z)},${opt.toInt(input$tank)}))
<#-- @formatter:on -->