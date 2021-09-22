<#include "mcitems.ftl">
(new Object() {
	public String get(BlockState _bs, String property) {
		Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty(property);
		return _prop instanceof EnumProperty _ep ? _bs.getValue(_ep).toString() : "";
}}.get(${mappedBlockToBlockStateCode(input$block)}, ${input$property}))