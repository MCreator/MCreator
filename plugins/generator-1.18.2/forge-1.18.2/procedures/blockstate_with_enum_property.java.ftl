<#include "mcitems.ftl">
/*@BlockState*/(new Object() {
	public BlockState with(BlockState _bs, String property, String newValue) {
		Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty(property);
		return _prop instanceof EnumProperty _ep && _ep.getValue(newValue).isPresent() ? _bs.setValue(_ep, (Enum) _ep.getValue(newValue).get()) : _bs;
}}.with(${mappedBlockToBlockStateCode(input$block)}, ${input$property}, ${input$value}))