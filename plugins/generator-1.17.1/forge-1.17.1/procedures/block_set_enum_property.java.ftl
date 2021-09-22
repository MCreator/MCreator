{
	String _value = ${input$value};
	BlockPos _pos = new BlockPos((int)${input$x},(int)${input$y},(int)${input$z});
	BlockState _bs =  world.getBlockState(_pos);
	Property<?> _property = _bs.getBlock().getStateDefinition().getProperty(${input$property});
	if (_property instanceof EnumProperty _enumProp && _property.getValue(_value).isPresent())
		world.setBlock(_pos, _bs.setValue(_enumProp, (Enum) _enumProp.getValue(_value).get()), 3);
}