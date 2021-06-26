<#include "mcitems.ftl">
(new Object() {
	public boolean get(BlockState _bs, String property) {
		BooleanProperty _prop = (BooleanProperty) _bs.getBlock().getStateContainer().getProperty(property);
		return _prop != null ? _bs.get(_prop) : false;
	}
}.get(${mappedBlockToBlockStateCode(input$block)}, ${input$property}))