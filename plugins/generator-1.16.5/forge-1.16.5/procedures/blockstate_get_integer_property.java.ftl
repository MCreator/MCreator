<#include "mcitems.ftl">
/*@int*/(new Object() {
	public int get(BlockState _bs, String property) {
		Property<?> _prop = _bs.getBlock().getStateContainer().getProperty(property);
		return _prop instanceof IntegerProperty ? _bs.get((IntegerProperty)_prop) : -1;
}}.get(${mappedBlockToBlockStateCode(input$block)}, ${input$property}))