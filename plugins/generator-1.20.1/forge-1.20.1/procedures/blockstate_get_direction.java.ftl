<#include "mcitems.ftl">
(new Object() {
	public Direction getDirection(BlockState _bs) {
		Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty("facing");
		if (_prop instanceof DirectionProperty _dp) return _bs.getValue(_dp);
		_prop = _bs.getBlock().getStateDefinition().getProperty("axis");
		return _prop instanceof EnumProperty _ep && _ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ?
			Direction.fromAxisAndDirection((Direction.Axis) _bs.getValue(_ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
}}.getDirection(${mappedBlockToBlockStateCode(input$block)}))