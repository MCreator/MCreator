<#include "mcelements.ftl">
{
	int _value = ${opt.toInt(input$value)};
	BlockPos _pos = ${toBlockPos(input$x,input$y,input$z)};
	BlockState _bs =  world.getBlockState(_pos);
	Property<?> _property = _bs.getBlock().getStateContainer().getProperty(${input$property});
	if (_property instanceof IntegerProperty && _property.getAllowedValues().contains(_value))
		world.setBlockState(_pos, _bs.with((IntegerProperty) _property, _value), 3);
}