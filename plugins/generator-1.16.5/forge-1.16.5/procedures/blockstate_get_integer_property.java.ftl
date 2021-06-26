<#include "mcitems.ftl">
(new Object() {
	public int get(BlockState _bs, String property) {
		IntegerProperty _prop = (IntegerProperty) _bs.getBlock().getStateContainer().getProperty(property);
		return _prop != null ? _bs.get(_prop) : -1;
	}
}.get(${mappedBlockToBlockStateCode(input$block)}, ${input$property}))