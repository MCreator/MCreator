private static int drainTankSimulate(LevelAccessor level, BlockPos pos, int amount, Direction direction) {
	if (level instanceof ILevelExtension levelExtension) {
		if (levelExtension.getCapability(Capabilities.Fluid.BLOCK, pos, direction) instanceof ResourceHandler<FluidResource> fluidHandler)
			return fluidHandler.drain(amount, IFluidHandler.FluidAction.SIMULATE).getAmount();
	}
	return 0;
}