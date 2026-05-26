private static int fillTankSimulate(LevelAccessor level, BlockPos pos, int amount, Direction direction, Fluid fluid) {
	if (amount > 0 && level instanceof ILevelExtension levelExtension) {
		if (levelExtension.getCapability(Capabilities.Fluid.BLOCK, pos, direction) instanceof ResourceHandler<FluidResource> fluidHandler) {
			try (var tx = Transaction.openRoot()) {
				return fluidHandler.insert(FluidResource.of(fluid), amount, tx);
			}
		}
	}
	return 0;
}