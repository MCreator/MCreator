<#-- @formatter:off -->
/*@int*/(new Object(){
	public int drainTankSimulate(LevelAccessor level, BlockPos pos, int amount) {
		AtomicInteger _retval = new AtomicInteger(0);
		BlockEntity _ent = level.getBlockEntity(pos);
		if (_ent != null)
			_ent.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, ${input$direction}).ifPresent(capability ->
				_retval.set(capability.drain(amount, IFluidHandler.FluidAction.SIMULATE).getAmount()));
		return _retval.get();
	}
}.drainTankSimulate(world, new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}),${opt.toInt(input$amount)}))
<#-- @formatter:on -->