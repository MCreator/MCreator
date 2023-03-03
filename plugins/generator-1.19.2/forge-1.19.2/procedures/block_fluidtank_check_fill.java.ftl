<#include "mcelements.ftl">
<#-- @formatter:off -->
/*@int*/(new Object(){
	public int fillTankSimulate(LevelAccessor _level, BlockPos _pos, int _amount) {
		AtomicInteger _retval = new AtomicInteger(0);
		BlockEntity _ent = _level.getBlockEntity(_pos);
		if (_ent != null)
			_ent.getCapability(ForgeCapabilities.FLUID_HANDLER, ${input$direction}).ifPresent(_capability ->
				<#if field$fluid.startsWith("CUSTOM:")>
				<#assign fluid = field$fluid?replace("CUSTOM:", "")>
				_retval.set(_capability.fill(new FluidStack(${JavaModName}Fluids.${fluid?ends_with(":Flowing")?then("FLOWING_","")}${generator.getRegistryNameForModElement(fluid?remove_ending(":Flowing"))?upper_case}.get(), _amount), IFluidHandler.FluidAction.SIMULATE))
				<#else>
				_retval.set(_capability.fill(new FluidStack(Fluids.${generator.map(field$fluid, "fluids")}, _amount), IFluidHandler.FluidAction.SIMULATE))
				</#if>
		);
		return _retval.get();
	}
}.fillTankSimulate(world, ${toBlockPos(input$x,input$y,input$z)},${opt.toInt(input$amount)}))
<#-- @formatter:on -->