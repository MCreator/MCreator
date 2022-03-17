<#-- @formatter:off -->
/*@int*/(new Object(){
	public int getEnergyStored(LevelAccessor level, BlockPos pos) {
		AtomicInteger _retval = new AtomicInteger(0);
		BlockEntity _ent = level.getBlockEntity(pos);
		if (_ent != null)
			_ent.getCapability(CapabilityEnergy.ENERGY, ${input$direction}).ifPresent(capability ->
				_retval.set(capability.getEnergyStored()));
		return _retval.get();
	}
}.getEnergyStored(world, new BlockPos((int)${input$x},(int)${input$y},(int)${input$z})))
<#-- @formatter:on -->