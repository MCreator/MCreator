<#-- @formatter:off -->
(new Object(){
	public int extractEnergySimulate(BlockPos pos, int _amount) {
		AtomicInteger _retval = new AtomicInteger(0);
		TileEntity _ent = world.getTileEntity(pos);
		if (_ent != null)
			_ent.getCapability(CapabilityEnergy.ENERGY, ${input$direction}).ifPresent(capability ->
				_retval.set(capability.extractEnergy(_amount, true)));
		return _retval.get();
	}
}.extractEnergySimulate(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}),(int)${input$amount}))
<#-- @formatter:on -->