<#include "mcitems.ftl">
(new Object() {
	public String get(BlockState _bs, String property) {
		EnumProperty<?> _prop = (EnumProperty) _bs.getBlock().getStateContainer().getProperty(property);
		return _prop != null ? _bs.get(_prop).toString() : "";
	}
}.get(${mappedBlockToBlockStateCode(input$block)}, ${input$property}))