<#include "mcitems.ftl">
/*@BlockState*/(new Object() {
	public BlockState with(BlockState _bs, String _property, boolean _newValue) {
		Property<?> _prop = _bs.getBlock().getStateContainer().getProperty(_property);
		return _prop instanceof BooleanProperty ? _bs.with((BooleanProperty) _prop, _newValue) : _bs;
}}.with(${mappedBlockToBlockStateCode(input$block)}, ${input$property}, ${input$value}))