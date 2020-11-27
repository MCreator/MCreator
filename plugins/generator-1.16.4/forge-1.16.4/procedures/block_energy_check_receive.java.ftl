<#-- @formatter:off -->
(new Object(){
	public int receiveEnergySimulate(BlockPos pos, int _amount) {
		AtomicInteger _retval = new AtomicInteger(0);
		TileEntity _ent = world.getTileEntity(pos);
		if (_ent != null)
			_ent.getCapability(CapabilityEnergy.ENERGY, ${input$direction}).ifPresent(capability ->
				_retval.set(capability.receiveEnergy(_amount, true)));
		return _retval.get();
	}
}.receiveEnergySimulate(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}),(int)${input$amount}))
<#-- @formatter:on -->