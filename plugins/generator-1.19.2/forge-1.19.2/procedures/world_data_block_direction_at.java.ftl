<#include "mcelements.ftl">
(new Object() {
	public Direction getDirection(LevelAccessor _world, BlockPos _pos){
		BlockState _bs = _world.getBlockState(_pos);
		Property<?> _property = _bs.getBlock().getStateDefinition().getProperty("facing");
		if (_property != null && _bs.getValue(_property) instanceof Direction _dir)
			return _dir;
		_property = _bs.getBlock().getStateDefinition().getProperty("axis");
		if (_property != null && _bs.getValue(_property) instanceof Direction.Axis _axis)
			return Direction.fromAxisAndDirection(_axis, Direction.AxisDirection.POSITIVE);
		return Direction.NORTH;
}}.getDirection(world, ${toBlockPos(input$x,input$y,input$z)}))