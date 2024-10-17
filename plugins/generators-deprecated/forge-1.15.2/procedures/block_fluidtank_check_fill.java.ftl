<#-- @formatter:off -->
(new Object(){
	public int fillTankSimulate(IWorld world, BlockPos pos, int amount) {
		AtomicInteger _retval = new AtomicInteger(0);
		TileEntity _ent = world.getTileEntity(pos);
		if (_ent != null)
			_ent.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, ${input$direction}).ifPresent(capability ->
				<#if field$fluid.startsWith("CUSTOM:")>
					<#if field$fluid.endsWith(":Flowing")>
					_retval.set(capability.fill(new FluidStack(${(field$fluid.replace("CUSTOM:", "").replace(":Flowing", ""))}Block.flowing, amount), IFluidHandler.FluidAction.SIMULATE))
					<#else>
					_retval.set(capability.fill(new FluidStack(${(field$fluid.replace("CUSTOM:", ""))}Block.still, amount), IFluidHandler.FluidAction.SIMULATE))
					</#if>
				<#else>
				_retval.set(capability.fill(new FluidStack(Fluids.${generator.map(field$fluid, "fluid")}, amount), IFluidHandler.FluidAction.SIMULATE))
				</#if>
		);
		return _retval.get();
	}
}.fillTankSimulate(world, new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}),(int)${input$amount}))
<#-- @formatter:on -->