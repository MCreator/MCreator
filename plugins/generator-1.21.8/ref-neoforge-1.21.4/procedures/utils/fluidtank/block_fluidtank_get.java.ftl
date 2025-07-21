private static int getFluidTankLevel(LevelAccessor level, BlockPos pos, int tank, Direction direction) {
	if (level instanceof ILevelExtension levelExtension) {
		IFluidHandler fluidHandler = levelExtension.getCapability(Capabilities.FluidHandler.BLOCK, pos, direction);
		if (fluidHandler != null)
			return fluidHandler.getFluidInTank(tank).getAmount();
	}
	return 0;
}