private static BlockState blockStateWithDirection(BlockState blockState, Direction newValue) {
	if (blockState.getBlock().getStateDefinition().getProperty("facing") instanceof EnumProperty enumProperty && enumProperty.getPossibleValues().contains(newValue))
		return blockState.setValue(enumProperty, newValue);
	if (blockState.getBlock().getStateDefinition().getProperty("axis") instanceof EnumProperty enumProperty && enumProperty.getPossibleValues().contains(newValue.getAxis()))
		return blockState.setValue(enumProperty, newValue.getAxis());
	return blockState;
}