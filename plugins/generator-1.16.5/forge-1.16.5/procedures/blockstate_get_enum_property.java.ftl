<#include "mcitems.ftl">
(new Object() {
	public String get(BlockState _bs, String property) {
		Property<?> _prop = _bs.getBlock().getStateContainer().getProperty(property);
		return _prop instanceof EnumProperty ? _bs.get((EnumProperty<?>)_prop).toString() : "";
}}.get(${mappedBlockToBlockStateCode(input$block)}, ${input$property}))