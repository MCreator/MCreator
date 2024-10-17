<#include "mcitems.ftl">
(new Object() {
	public String get(BlockState _bs, String _property) {
		Property<?> _prop = _bs.getBlock().getStateContainer().getProperty(_property);
		return _prop instanceof EnumProperty ? _bs.get((EnumProperty<?>)_prop).toString() : "";
}}.get(${mappedBlockToBlockStateCode(input$block)}, ${input$property}))