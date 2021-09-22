{
	BlockState _bs =  world.getBlockState(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}));
	Property<?> _property = _bs.getBlock().getStateContainer().getProperty(${input$property});
	if (_property instanceof EnumProperty && _property.parseValue(${input$value}).isPresent())
		world.setBlockState(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}), _bs.with((EnumProperty) _property, (Enum) _property.parseValue(${input$value}).get()), 3);
}