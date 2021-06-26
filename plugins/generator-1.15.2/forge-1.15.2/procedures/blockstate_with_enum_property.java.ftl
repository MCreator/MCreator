<#include "mcitems.ftl">
/*@BlockState*/(new Object() {
	public BlockState with(BlockState _bs, String property, String newValue) {
		try {
			EnumProperty _prop = (EnumProperty) _bs.getBlock().getStateContainer().getProperty(property);
			return _bs.with(_prop, (Enum) _prop.parseValue(newValue).get());
		} catch (Exception e) {
			return _bs;
		}
}}.with(${mappedBlockToBlockStateCode(input$block)}, ${input$property}, ${input$value}))