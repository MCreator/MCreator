<#include "mcelements.ftl">
<#-- @formatter:off -->
/*@int*/(new Object(){
	public int fillTankSimulate(LevelAccessor level, BlockPos pos, int amount) {
		if (level instanceof ILevelExtension _ext) {
			IFluidHandler _fluidHandler = _ext.getCapability(Capabilities.FluidHandler.BLOCK, pos, ${input$direction});
			if (_fluidHandler != null)
				<#if field$fluid.startsWith("CUSTOM:")>
				<#assign fluid = field$fluid?replace("CUSTOM:", "")>
				return _fluidHandler.fill(new FluidStack(${JavaModName}Fluids.${fluid?ends_with(":Flowing")?then("FLOWING_","")}${generator.getRegistryNameForModElement(fluid?remove_ending(":Flowing"))?upper_case}.get(), amount), IFluidHandler.FluidAction.SIMULATE);
				<#else>
				return _fluidHandler.fill(new FluidStack(Fluids.${generator.map(field$fluid, "fluids")}, amount), IFluidHandler.FluidAction.SIMULATE);
				</#if>
		}
		return 0;
	}
}.fillTankSimulate(world, ${toBlockPos(input$x,input$y,input$z)},${opt.toInt(input$amount)}))
<#-- @formatter:on -->