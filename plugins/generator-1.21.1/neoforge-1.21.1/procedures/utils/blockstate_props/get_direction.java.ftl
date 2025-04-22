private static Direction getDirectionFromBlockState(BlockState blockState) {
	Property<?> prop = blockState.getBlock().getStateDefinition().getProperty("facing");
	if (prop instanceof DirectionProperty dp) return blockState.getValue(dp);
	prop = blockState.getBlock().getStateDefinition().getProperty("axis");
	return prop instanceof EnumProperty ep && ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ?
		Direction.fromAxisAndDirection((Direction.Axis) blockState.getValue(ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
}