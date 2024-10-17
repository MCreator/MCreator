<#include "mcelements.ftl">
try {
	BlockState _bs =  world.getBlockState(${toBlockPos(input$x,input$y,input$z)});
	DirectionProperty _property = (DirectionProperty) _bs.getBlock().getStateContainer().getProperty("facing");
	if (_property != null) {
		world.setBlockState(${toBlockPos(input$x,input$y,input$z)},
			_bs.with(_property, ${input$direction}), 3);
	} else {
		world.setBlockState(${toBlockPos(input$x,input$y,input$z)},
			_bs.with((EnumProperty<Direction.Axis>) _bs.getBlock().getStateContainer().getProperty("axis"),
			${input$direction}.getAxis()), 3);
	}
} catch (Exception e) {
}