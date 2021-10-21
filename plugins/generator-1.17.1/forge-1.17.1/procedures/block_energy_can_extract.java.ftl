<#-- @formatter:off -->
(new Object(){
	public boolean canExtractEnergy(LevelAccessor level, BlockPos pos) {
		AtomicBoolean _retval = new AtomicBoolean(false);
		BlockEntity _ent = level.getBlockEntity(pos);
		if (_ent != null)
			_ent.getCapability(CapabilityEnergy.ENERGY, ${input$direction}).ifPresent(capability ->
				_retval.set(capability.canExtract()));
		return _retval.get();
	}
}.canExtractEnergy(world, new BlockPos((int)${input$x},(int)${input$y},(int)${input$z})))
<#-- @formatter:on -->