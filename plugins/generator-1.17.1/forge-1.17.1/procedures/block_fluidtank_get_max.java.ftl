<#-- @formatter:off -->
/*@int*/(new Object() {
	public int getFluidTankCapacity(LevelAccessor level, BlockPos pos, int tank) {
		AtomicInteger _retval = new AtomicInteger(0);
		BlockEntity _ent = level.getBlockEntity(pos);
		if (_ent != null)
			_ent.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, ${input$direction}).ifPresent(capability ->
				_retval.set(capability.getTankCapacity(tank)));
		return _retval.get();
	}
}.getFluidTankCapacity(world, new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}),${opt.toInt(input$tank)}))
<#-- @formatter:on -->