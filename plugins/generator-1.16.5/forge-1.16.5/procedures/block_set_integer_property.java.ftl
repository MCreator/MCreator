{
	BlockState _bs =  world.getBlockState(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}));
	Property<?> _property = _bs.getBlock().getStateContainer().getProperty(${input$property});
	if (_property instanceof IntegerProperty && _property.getAllowedValues().contains(${input$value}))
		world.setBlockState(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}), _bs.with((IntegerProperty) _property, (int) ${input$value}), 3);
}