<#include "mcelements.ftl">
(new Object() {
	public Direction getDirection(BlockPos pos){
		BlockState _bs = world.getBlockState(pos);
		Property<?> property = _bs.getBlock().getStateDefinition().getProperty("facing");
		if (property != null && _bs.getValue(property) instanceof Direction _dir)
			return _dir;
		else if (_bs.hasProperty(BlockStateProperties.AXIS))
			return Direction.fromAxisAndDirection(_bs.getValue(BlockStateProperties.AXIS), Direction.AxisDirection.POSITIVE);
		else if (_bs.hasProperty(BlockStateProperties.HORIZONTAL_AXIS))
			return Direction.fromAxisAndDirection(_bs.getValue(BlockStateProperties.HORIZONTAL_AXIS), Direction.AxisDirection.POSITIVE);
		return Direction.NORTH;
}}.getDirection(${toBlockPos(input$x,input$y,input$z)}))