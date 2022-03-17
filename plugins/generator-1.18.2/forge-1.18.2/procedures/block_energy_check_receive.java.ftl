<#-- @formatter:off -->
/*@int*/(new Object(){
	public int receiveEnergySimulate(LevelAccessor level, BlockPos pos, int _amount) {
		AtomicInteger _retval = new AtomicInteger(0);
		BlockEntity _ent = level.getBlockEntity(pos);
		if (_ent != null)
			_ent.getCapability(CapabilityEnergy.ENERGY, ${input$direction}).ifPresent(capability ->
				_retval.set(capability.receiveEnergy(_amount, true)));
		return _retval.get();
	}
}.receiveEnergySimulate(world, new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}),${opt.toInt(input$amount)}))
<#-- @formatter:on -->