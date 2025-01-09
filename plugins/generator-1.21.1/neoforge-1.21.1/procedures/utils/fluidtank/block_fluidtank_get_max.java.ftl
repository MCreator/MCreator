private static int getFluidTankCapacity(LevelAccessor level, BlockPos pos, int tank, Direction direction) {
	if (level instanceof ILevelExtension levelExtension) {
		IFluidHandler fluidHandler = levelExtension.getCapability(Capabilities.FluidHandler.BLOCK, pos, direction);
		if (fluidHandler != null)
			return fluidHandler.getTankCapacity(tank);
	}
	return 0;
}