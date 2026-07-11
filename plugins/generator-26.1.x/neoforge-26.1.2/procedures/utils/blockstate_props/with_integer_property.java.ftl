private static BlockState blockStateWithInt(BlockState blockState, String property, int newValue) {
	Property<?> prop = blockState.getBlock().getStateDefinition().getProperty(property);
	return prop instanceof IntegerProperty ip && prop.getPossibleValues().contains(newValue) ? blockState.setValue(ip, newValue) : blockState;
}