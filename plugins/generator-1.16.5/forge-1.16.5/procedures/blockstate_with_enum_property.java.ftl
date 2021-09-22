<#include "mcitems.ftl">
/*@BlockState*/(new Object() {
	public BlockState with(BlockState _bs, String property, String newValue) {
		Property<?> _prop = _bs.getBlock().getStateContainer().getProperty(property);
		if (_prop instanceof EnumProperty && _prop.parseValue(newValue).isPresent())
			return _bs.with((EnumProperty)_prop, (Enum) _prop.parseValue(newValue).get());
		return _bs;
}}.with(${mappedBlockToBlockStateCode(input$block)}, ${input$property}, ${input$value}))