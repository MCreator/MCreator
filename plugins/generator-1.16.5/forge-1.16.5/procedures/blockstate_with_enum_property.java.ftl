<#include "mcitems.ftl">
/*@BlockState*/(new Object() {
	public BlockState with(BlockState _bs, String _property, String _newValue) {
		Property<?> _prop = _bs.getBlock().getStateContainer().getProperty(_property);
		return _prop instanceof EnumProperty && _prop.parseValue(_newValue).isPresent() ? _bs.with((EnumProperty)_prop, (Enum) _prop.parseValue(_newValue).get()) : _bs;
}}.with(${mappedBlockToBlockStateCode(input$block)}, ${input$property}, ${input$value}))