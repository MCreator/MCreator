<#include "mcitems.ftl">
/*@BlockState*/(new Object() {
	public BlockState with(BlockState _bs, Direction newValue) {
		Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty("facing");
		if (_prop instanceof DirectionProperty _dp && _dp.getPossibleValues().contains(newValue)) return _bs.setValue(_dp, newValue);
		_prop = _bs.getBlock().getStateDefinition().getProperty("axis");
		return _prop instanceof EnumProperty _ep && _ep.getPossibleValues().contains(newValue.getAxis()) ? _bs.setValue(_ep, newValue.getAxis()) : _bs;
}}.with(${mappedBlockToBlockStateCode(input$block)}, ${input$value}))