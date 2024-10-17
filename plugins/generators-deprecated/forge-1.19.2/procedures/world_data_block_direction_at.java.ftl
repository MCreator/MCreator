<#include "mcelements.ftl">
(new Object() {
	public Direction getDirection(BlockPos pos){
		BlockState _bs = world.getBlockState(pos);
		Property<?> property = _bs.getBlock().getStateDefinition().getProperty("facing");
		if (property != null && _bs.getValue(property) instanceof Direction _dir)
			return _dir;
		property = _bs.getBlock().getStateDefinition().getProperty("axis");
		if (property != null && _bs.getValue(property) instanceof Direction.Axis _axis)
			return Direction.fromAxisAndDirection(_axis, Direction.AxisDirection.POSITIVE);
		return Direction.NORTH;
}}.getDirection(${toBlockPos(input$x,input$y,input$z)}))