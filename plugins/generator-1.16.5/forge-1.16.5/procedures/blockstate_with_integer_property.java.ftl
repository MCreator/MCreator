<#include "mcitems.ftl">
/*@BlockState*/(new Object() {
	public BlockState with(BlockState _bs, String _property, int _newValue) {
		Property<?> _prop = _bs.getBlock().getStateContainer().getProperty(_property);
		return _prop instanceof IntegerProperty && _prop.getAllowedValues().contains(_newValue) ? _bs.with((IntegerProperty) _prop, _newValue) : _bs;
}}.with(${mappedBlockToBlockStateCode(input$block)}, ${input$property}, ${opt.toInt(input$value)}))