private static boolean canReceiveEnergy(LevelAccessor level, BlockPos pos, Direction direction) {
	if (level instanceof ILevelExtension levelExtension) {
		IEnergyStorage energyStorage = levelExtension.getCapability(Capabilities.EnergyStorage.BLOCK, pos, direction);
		if (energyStorage != null)
			return energyStorage.canReceive();
	}
	return false;
}