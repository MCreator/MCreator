private static int fillTankSimulate(LevelAccessor level, BlockPos pos, int amount, Direction direction, Fluid fluid) {
	if (level instanceof ILevelExtension levelExtension) {
		if (levelExtension.getCapability(Capabilities.Fluid.BLOCK, pos, direction) instanceof ResourceHandler<FluidResource> fluidHandler)
			return fluidHandler.fill(new FluidStack(fluid, amount), IFluidHandler.FluidAction.SIMULATE);
	}
	return 0;
}