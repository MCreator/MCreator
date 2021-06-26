<#include "mcitems.ftl">
/*@BlockState*/(new Object() {
	public BlockState with(BlockState _bs, String property, int newValue) {
		try {
			IntegerProperty _prop = (IntegerProperty) _bs.getBlock().getStateContainer().getProperty(property);
			return _bs.with(_prop, newValue);
		} catch (Exception e) {
			return _bs;
		}
}}.with(${mappedBlockToBlockStateCode(input$block)}, ${input$property}, (int) ${input$value}))