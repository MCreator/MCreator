private static int extractEnergySimulate(LevelAccessor level, BlockPos pos, int amount, Direction direction) {
	if (level instanceof ILevelExtension levelExtension) {
		IEnergyStorage energyStorage = levelExtension.getCapability(Capabilities.EnergyStorage.BLOCK, pos, direction);
		if (energyStorage != null)
			return energyStorage.extractEnergy(amount, true);
	}
	return 0;
}