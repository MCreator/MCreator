<#include "mcitems.ftl">
/*@BlockState*/(new Object() {
	public BlockState with(BlockState _bs, String property, int newValue) {
		Property<?> _prop = _bs.getBlock().getStateContainer().getProperty(property);
		if (_prop instanceof IntegerProperty && _prop.getAllowedValues().contains(newValue))
			return _bs.with((IntegerProperty) _prop, newValue);
		return _bs;
}}.with(${mappedBlockToBlockStateCode(input$block)}, ${input$property}, (int) ${input$value}))