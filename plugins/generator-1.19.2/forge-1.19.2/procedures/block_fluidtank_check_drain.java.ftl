<#include "mcelements.ftl">
<#-- @formatter:off -->
/*@int*/(new Object(){
	public int drainTankSimulate(LevelAccessor _level, BlockPos _pos, int _amount) {
		AtomicInteger _retval = new AtomicInteger(0);
		BlockEntity _ent = _level.getBlockEntity(_pos);
		if (_ent != null)
			_ent.getCapability(ForgeCapabilities.FLUID_HANDLER, ${input$direction}).ifPresent(_capability ->
				_retval.set(_capability.drain(_amount, IFluidHandler.FluidAction.SIMULATE).getAmount()));
		return _retval.get();
	}
}.drainTankSimulate(world, ${toBlockPos(input$x,input$y,input$z)},${opt.toInt(input$amount)}))
<#-- @formatter:on -->