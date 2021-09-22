<#include "mcitems.ftl">
/*@BlockState*/(new Object() {
	public BlockState with(BlockState _bs, String property, boolean newValue) {
		Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty(property);
		return _prop instanceof BooleanProperty _bp ? _bs.setValue(_bp, newValue) : _bs;
}}.with(${mappedBlockToBlockStateCode(input$block)}, ${input$property}, ${input$value}))