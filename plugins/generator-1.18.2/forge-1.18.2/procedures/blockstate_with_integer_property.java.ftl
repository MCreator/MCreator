<#include "mcitems.ftl">
/*@BlockState*/(new Object() {
	public BlockState with(BlockState _bs, String property, int newValue) {
		Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty(property);
		return _prop instanceof IntegerProperty _ip && _prop.getPossibleValues().contains(newValue) ? _bs.setValue(_ip, newValue) : _bs;
}}.with(${mappedBlockToBlockStateCode(input$block)}, ${input$property}, ${opt.toInt(input$value)}))