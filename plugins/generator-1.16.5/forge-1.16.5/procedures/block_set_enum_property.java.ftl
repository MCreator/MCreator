{
	String _value = ${input$value};
	BlockPos _pos = new BlockPos((int)${input$x},(int)${input$y},(int)${input$z});
	BlockState _bs =  world.getBlockState(_pos);
	Property<?> _property = _bs.getBlock().getStateContainer().getProperty(${input$property});
	if (_property instanceof EnumProperty && _property.parseValue(_value).isPresent())
		world.setBlockState(_pos, _bs.with((EnumProperty) _property, (Enum) _property.parseValue(_value).get()), 3);
}