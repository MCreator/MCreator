<#-- @formatter:off -->
(new Object() {
	public int getFluidTankLevel(BlockPos pos, int tank) {
		AtomicInteger _retval = new AtomicInteger(0);
		TileEntity _ent = world.getTileEntity(pos);
		if (_ent != null)
			_ent.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, ${input$direction}).ifPresent(capability ->
				_retval.set(capability.getFluidInTank(tank).getAmount()));
		return _retval.get();
	}
}.getFluidTankLevel(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}), (int)${input$tank}))
<#-- @formatter:on -->