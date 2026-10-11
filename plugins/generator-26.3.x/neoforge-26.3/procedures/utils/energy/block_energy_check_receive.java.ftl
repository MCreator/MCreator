private static int receiveEnergySimulate(LevelAccessor level, BlockPos pos, int amount, Direction direction) {
	if (level instanceof ILevelExtension levelExtension) {
		if (levelExtension.getCapability(Capabilities.Energy.BLOCK, pos, direction) instanceof EnergyHandler energyHandler) {
			try (var tx = Transaction.openRoot()) {
				return energyHandler.insert(amount, tx);
			}
		}
	}
	return 0;
}