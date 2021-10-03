<#-- @formatter:off -->
/*@int*/(new Object(){
	public int fillTankSimulate(LevelAccessor level, BlockPos pos, int amount) {
		AtomicInteger _retval = new AtomicInteger(0);
		BlockEntity _ent = level.getBlockEntity(pos);
		if (_ent != null)
			_ent.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, ${input$direction}).ifPresent(capability ->
				<#if field$fluid.startsWith("CUSTOM:")>
				<#assign fluid = field$fluid?replace("CUSTOM:", "")>
				_retval.set(capability.fill(new FluidStack(${JavaModName}Fluids.${fluid?ends_with(":Flowing")?then("FLOWING_","")}${generator.getRegistryNameForModElement(fluid?remove_ending(":Flowing"))?upper_case}, amount), IFluidHandler.FluidAction.SIMULATE))
				<#else>
				_retval.set(capability.fill(new FluidStack(Fluids.${generator.map(field$fluid, "fluid")}, amount), IFluidHandler.FluidAction.SIMULATE))
				</#if>
		);
		return _retval.get();
	}
}.fillTankSimulate(world, new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}),${opt.toInt(input$amount)}))
<#-- @formatter:on -->