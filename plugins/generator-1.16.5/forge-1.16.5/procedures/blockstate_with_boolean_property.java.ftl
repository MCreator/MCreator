<#include "mcitems.ftl">
/*@BlockState*/(new Object() {
	public BlockState with(BlockState _bs, String property, boolean newValue) {
		try {
			BooleanProperty _prop = (BooleanProperty) _bs.getBlock().getStateContainer().getProperty(property);
			return _bs.with(_prop, newValue);
		} catch (Exception e) {
			return _bs;
		}
}}.with(${mappedBlockToBlockStateCode(input$block)}, ${input$property}, ${input$value}))