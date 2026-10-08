private static BlockState blockStateWithEnum(BlockState blockState, String property, String newValue) {
	Property<?> prop = blockState.getBlock().getStateDefinition().getProperty(property);
	return prop instanceof EnumProperty ep && ep.getValue(newValue).isPresent() ? blockState.setValue(ep, (Enum) ep.getValue(newValue).get()) : blockState;
}