<#-- @formatter:off -->
/*@int*/(new Object() {
	public int getBlockTanks(LevelAccessor level, BlockPos pos) {
		AtomicInteger _retval = new AtomicInteger(0);
		BlockEntity _ent = level.getBlockEntity(pos);
		if (_ent != null)
			_ent.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, ${input$direction}).ifPresent(capability ->
				_retval.set(capability.getTanks()));
		return _retval.get();
	}
}.getBlockTanks(world, new BlockPos((int)${input$x},(int)${input$y},(int)${input$z})))
<#-- @formatter:on -->