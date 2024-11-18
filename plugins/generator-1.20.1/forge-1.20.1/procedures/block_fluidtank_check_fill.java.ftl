<#include "mcelements.ftl">
<#-- @formatter:off -->
/*@int*/(new Object(){
	public int fillTankSimulate(LevelAccessor level, BlockPos pos, int amount) {
		AtomicInteger _retval = new AtomicInteger(0);
		BlockEntity _ent = level.getBlockEntity(pos);
		if (_ent != null)
			_ent.getCapability(ForgeCapabilities.FLUID_HANDLER, ${input$direction}).ifPresent(capability ->
				_retval.set(capability.fill(new FluidStack(${generator.map(field$fluid, "fluids")}, amount), IFluidHandler.FluidAction.SIMULATE))
		);
		return _retval.get();
	}
}.fillTankSimulate(world, ${toBlockPos(input$x,input$y,input$z)},${opt.toInt(input$amount)}))
<#-- @formatter:on -->