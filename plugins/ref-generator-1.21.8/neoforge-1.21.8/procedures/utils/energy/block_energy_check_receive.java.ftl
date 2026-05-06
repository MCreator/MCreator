private static int receiveEnergySimulate(LevelAccessor level, BlockPos pos, int amount, Direction direction) {
	if (level instanceof ILevelExtension levelExtension) {
		if (levelExtension.getCapability(Capabilities.Energy.BLOCK, pos, direction) instanceof EnergyHandler energyHandler)
			return energyStorage.receiveEnergy(amount, true);
	}
	return 0;
}