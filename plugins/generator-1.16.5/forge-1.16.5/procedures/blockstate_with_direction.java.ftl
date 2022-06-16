<#include "mcitems.ftl">
/*@BlockState*/(new Object() {
	public BlockState with(BlockState _bs, Direction newValue) {
		Property<?> _prop = _bs.getBlock().getStateContainer().getProperty("facing");
		if (_prop instanceof DirectionProperty && _prop.getAllowedValues().contains(newValue)) return _bs.with((DirectionProperty) _prop, newValue);
		_prop = _bs.getBlock().getStateContainer().getProperty("axis");
		return _prop instanceof EnumProperty && _prop.getAllowedValues().contains(newValue.getAxis()) ? _bs.with((EnumProperty<Direction.Axis>) _prop, newValue.getAxis()) : _bs;
}}.with(${mappedBlockToBlockStateCode(input$block)}, ${input$value}))