<#include "mcitems.ftl">
(new Object() {
	public int get(BlockState _bs, String property) {
		Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty(property);
		return _prop instanceof IntegerProperty _ip ? _bs.getValue(_ip) : -1;
}}.get(${mappedBlockToBlockStateCode(input$block)}, ${input$property}))