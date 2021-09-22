{
	BlockState _bs =  world.getBlockState(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}));
	Property<?> _property = _bs.getBlock().getStateContainer().getProperty(${input$property});
	if (_property instanceof BooleanProperty)
		world.setBlockState(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}), _bs.with((BooleanProperty) _property, ${input$value}), 3);
}