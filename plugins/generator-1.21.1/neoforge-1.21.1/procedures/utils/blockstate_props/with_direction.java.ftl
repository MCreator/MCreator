private static BlockState blockStateWithDirection(BlockState blockState, Direction newValue) {
	Property<?> prop = blockState.getBlock().getStateDefinition().getProperty("facing");
	if (prop instanceof DirectionProperty dp && dp.getPossibleValues().contains(newValue)) return blockState.setValue(dp, newValue);
	prop = blockState.getBlock().getStateDefinition().getProperty("axis");
	return prop instanceof EnumProperty ep && ep.getPossibleValues().contains(newValue.getAxis()) ? blockState.setValue(ep, newValue.getAxis()) : blockState;
}