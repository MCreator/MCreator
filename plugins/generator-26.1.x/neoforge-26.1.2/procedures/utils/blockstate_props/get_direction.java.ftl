<@addTemplate file="utils/blockstate_props/property_from_string.java.ftl"/>
private static Direction getDirectionFromBlockState(BlockState blockState) {
	if (getPropertyByName(blockState, "facing") instanceof EnumProperty ep && ep.getValueClass() == Direction.class)
		return (Direction) blockState.getValue(ep);
	if (getPropertyByName(blockState, "axis") instanceof EnumProperty ep && ep.getValueClass() == Direction.Axis.class)
		return Direction.fromAxisAndDirection((Direction.Axis) blockState.getValue(ep), Direction.AxisDirection.POSITIVE);
	return Direction.NORTH;
}