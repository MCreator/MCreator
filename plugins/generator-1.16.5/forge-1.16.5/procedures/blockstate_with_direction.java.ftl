<#include "mcitems.ftl">
/*@BlockState*/(new Object() {
	public BlockState with(BlockState _bs, Direction newValue) {
		try {
			DirectionProperty _prop = (DirectionProperty) _bs.getBlock().getStateContainer().getProperty("facing");
			if (_prop != null)
				return _bs.with(_prop, newValue);
			else
				return _bs.with((EnumProperty<Direction.Axis>) _bs.getBlock().getStateContainer().getProperty("axis"), newValue.getAxis());
		} catch (Exception e) {
			return _bs;
		}
}}.with(${mappedBlockToBlockStateCode(input$block)}, ${input$value}))