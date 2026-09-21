private static int drainTankSimulate(LevelAccessor level, BlockPos pos, int amount, Direction direction) {
	if (amount > 0 && level instanceof ILevelExtension levelExtension) {
		if (levelExtension.getCapability(Capabilities.Fluid.BLOCK, pos, direction) instanceof ResourceHandler<FluidResource> fluidHandler) {
			try (var tx = Transaction.openRoot()) {
				return ResourceHandlerUtil.extractFirst(fluidHandler, _ -> true, amount, tx).amount();
			}
		}
	}
	return 0;
}