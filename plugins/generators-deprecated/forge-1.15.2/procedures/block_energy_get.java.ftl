<#-- @formatter:off -->
(new Object(){
	public int getEnergyStored(IWorld world, BlockPos pos) {
		AtomicInteger _retval = new AtomicInteger(0);
		TileEntity _ent = world.getTileEntity(pos);
		if (_ent != null)
			_ent.getCapability(CapabilityEnergy.ENERGY, ${input$direction}).ifPresent(capability ->
				_retval.set(capability.getEnergyStored()));
		return _retval.get();
	}
}.getEnergyStored(world, new BlockPos((int)${input$x},(int)${input$y},(int)${input$z})))
<#-- @formatter:on -->