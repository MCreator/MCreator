{
	int _value = (int) ${input$value};
	BlockPos _pos = new BlockPos((int)${input$x},(int)${input$y},(int)${input$z});
	BlockState _bs = world.getBlockState(_pos);
	Property<?> _property = _bs.getBlock().getStateDefinition().getProperty(${input$property});
	if (_property instanceof IntegerProperty _integerProp && _property.getPossibleValues().contains(_value))
		world.setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
}