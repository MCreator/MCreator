<#include "mcelements.ftl">
{
	String _value = ${input$value};
	BlockPos _pos = ${toBlockPos(input$x,input$y,input$z)};
	BlockState _bs =  world.getBlockState(_pos);
	Property<?> _property = _bs.getBlock().getStateContainer().getProperty(${input$property});
	if (_property instanceof EnumProperty && _property.parseValue(_value).isPresent())
		world.setBlockState(_pos, _bs.with((EnumProperty) _property, (Enum) _property.parseValue(_value).get()), 3);
}