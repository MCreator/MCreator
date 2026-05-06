private static boolean canExtractEnergy(LevelAccessor level, BlockPos pos, Direction direction) {
	if (level instanceof ILevelExtension levelExtension) {
		if (levelExtension.getCapability(Capabilities.Energy.BLOCK, pos, direction) instanceof EnergyHandler energyHandler)
			return energyStorage.canExtract();
	}
	return false;
}