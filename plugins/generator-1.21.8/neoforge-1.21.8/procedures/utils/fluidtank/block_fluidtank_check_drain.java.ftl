private static int drainTankSimulate(LevelAccessor level, BlockPos pos, int amount, Direction direction) {
	if (level instanceof ILevelExtension levelExtension) {
		IFluidHandler fluidHandler = levelExtension.getCapability(Capabilities.FluidHandler.BLOCK, pos, direction);
		if (fluidHandler != null)
			return fluidHandler.drain(amount, IFluidHandler.FluidAction.SIMULATE).getAmount();
	}
	return 0;
}