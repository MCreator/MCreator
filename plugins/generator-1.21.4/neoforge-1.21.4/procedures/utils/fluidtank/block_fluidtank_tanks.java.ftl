private static int getBlockTanks(LevelAccessor level, BlockPos pos, Direction direction) {
	if (level instanceof ILevelExtension levelExtension) {
		IFluidHandler fluidHandler = levelExtension.getCapability(Capabilities.FluidHandler.BLOCK, pos, direction);
		if (fluidHandler != null)
			return fluidHandler.getTanks();
	}
	return 0;
}