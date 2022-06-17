<#include "mcitems.ftl">
(new Object() {
	public Direction getDirection(BlockState _bs) {
		Property<?> _prop = _bs.getBlock().getStateContainer().getProperty("facing");
		if (_prop instanceof DirectionProperty) return _bs.get((DirectionProperty)_prop);
		_prop = _bs.getBlock().getStateContainer().getProperty("axis");
		return _prop instanceof EnumProperty && _prop.getAllowedValues().toArray()[0] instanceof Direction.Axis ?
			Direction.getFacingFromAxisDirection(_bs.get((EnumProperty<Direction.Axis>) _prop), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
}}.getDirection(${mappedBlockToBlockStateCode(input$block)}))