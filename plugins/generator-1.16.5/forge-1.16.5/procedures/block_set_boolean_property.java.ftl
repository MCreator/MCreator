{
	BlockPos _pos = new BlockPos((int)${input$x},(int)${input$y},(int)${input$z});
	BlockState _bs =  world.getBlockState(_pos);
	Property<?> _property = _bs.getBlock().getStateContainer().getProperty(${input$property});
	if (_property instanceof BooleanProperty)
		world.setBlockState(_pos, _bs.with((BooleanProperty) _property, ${input$value}), 3);
}