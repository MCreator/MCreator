<#include "mcitems.ftl">
/*@BlockState*/(new Object() {
	public BlockState with(BlockState _bs, String property, boolean newValue) {
		Property<?> _prop = _bs.getBlock().getStateContainer().getProperty(property);
		if (_prop instanceof BooleanProperty)
			return _bs.with((BooleanProperty) _prop, newValue);
		return _bs;
}}.with(${mappedBlockToBlockStateCode(input$block)}, ${input$property}, ${input$value}))