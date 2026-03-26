private static int fillTankSimulate(LevelAccessor level, BlockPos pos, int amount, Direction direction, Fluid fluid) {
	if (level instanceof ILevelExtension levelExtension) {
		IFluidHandler fluidHandler = levelExtension.getCapability(Capabilities.FluidHandler.BLOCK, pos, direction);
		if (fluidHandler != null)
			return fluidHandler.fill(new FluidStack(fluid, amount), IFluidHandler.FluidAction.SIMULATE);
	}
	return 0;
}