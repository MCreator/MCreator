<#include "mcelements.ftl">
{
	Direction _dir = ${input$direction};
	BlockPos _pos = ${toBlockPos(input$x,input$y,input$z)};
	BlockState _bs =  world.getBlockState(_pos);
	if (_bs.getBlock().getStateDefinition().getProperty("facing") instanceof EnumProperty _dp && _dp.getPossibleValues().contains(_dir)) {
		world.setBlock(_pos, _bs.setValue(_dp, _dir), 3);
	} else if (_bs.getBlock().getStateDefinition().getProperty("axis") instanceof EnumProperty _ap && _ap.getPossibleValues().contains(_dir.getAxis())) {
		world.setBlock(_pos, _bs.setValue(_ap, _dir.getAxis()), 3);
	}
}