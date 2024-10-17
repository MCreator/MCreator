<#-- @formatter:off -->
(new Object(){
	public int drainTankSimulate(BlockPos pos, int amount) {
		AtomicInteger _retval = new AtomicInteger(0);
		TileEntity _ent = world.getTileEntity(pos);
		if (_ent != null)
			_ent.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, ${input$direction}).ifPresent(capability ->
				_retval.set(capability.drain(amount, IFluidHandler.FluidAction.SIMULATE).getAmount()));
		return _retval.get();
	}
}.drainTankSimulate(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}),(int)${input$amount}))
<#-- @formatter:on -->