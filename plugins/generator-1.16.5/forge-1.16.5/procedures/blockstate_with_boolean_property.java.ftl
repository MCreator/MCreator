<#include "mcitems.ftl">
/*@BlockState*/(new Object() {
	public BlockState with(BlockState _bs, String property, boolean newValue) {
		Property<?> _prop = _bs.getBlock().getStateContainer().getProperty(property);
		return _prop instanceof BooleanProperty ? _bs.with((BooleanProperty) _prop, newValue) : _bs;
}}.with(${mappedBlockToBlockStateCode(input$block)}, ${input$property}, ${input$value}))