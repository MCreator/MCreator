<#-- @formatter:off -->
(new Object(){
	public int getMaxEnergyStored(BlockPos pos) {
		AtomicInteger _retval = new AtomicInteger(0);
		TileEntity _ent = world.getTileEntity(pos);
		if (_ent != null)
			_ent.getCapability(CapabilityEnergy.ENERGY, ${input$direction}).ifPresent(capability ->
				_retval.set(capability.getMaxEnergyStored()));
		return _retval.get();
	}
}.getMaxEnergyStored(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z})))
<#-- @formatter:on -->