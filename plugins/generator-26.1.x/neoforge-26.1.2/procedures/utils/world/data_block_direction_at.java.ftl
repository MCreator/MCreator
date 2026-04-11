private static Direction getBlockDirection(LevelAccessor world, BlockPos pos) {
	BlockState blockState = world.getBlockState(pos);
	Property<?> property = blockState.getBlock().getStateDefinition().getProperty("facing");
	if (property != null && blockState.getValue(property) instanceof Direction direction)
		return direction;
	else if (blockState.hasProperty(BlockStateProperties.AXIS))
		return Direction.fromAxisAndDirection(blockState.getValue(BlockStateProperties.AXIS), Direction.AxisDirection.POSITIVE);
	else if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_AXIS))
		return Direction.fromAxisAndDirection(blockState.getValue(BlockStateProperties.HORIZONTAL_AXIS), Direction.AxisDirection.POSITIVE);
	return Direction.NORTH;
}