<@addTemplate file="utils/blockstate_props/property_from_string.java.ftl"/>
private static Direction getDirectionFromBlockState(BlockState blockState) {
	Property<?> prop = getPropertyByName(blockState, "facing");
	if (prop instanceof DirectionProperty dp) return blockState.getValue(dp);
	prop = getPropertyByName(blockState, "axis");
	return prop instanceof EnumProperty ep && ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ?
		Direction.fromAxisAndDirection((Direction.Axis) blockState.getValue(ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
}