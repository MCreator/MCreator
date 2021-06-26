<#include "mcitems.ftl">
(new Object() {
	public Direction getDirection(BlockState _bs) {
		try {
		DirectionProperty property = (DirectionProperty) _bs.getBlock().getStateContainer().getProperty("facing");
		if (property != null)
			return _bs.get(property);
		return Direction.getFacingFromAxisDirection(_bs.get((EnumProperty<Direction.Axis>) _bs.getBlock()
			.getStateContainer().getProperty("axis")), Direction.AxisDirection.POSITIVE);
		} catch (Exception e) {
			return Direction.NORTH;
		}
	}
}.getDirection(${mappedBlockToBlockStateCode(input$block)}))