{
	BlockPos _pos = new BlockPos((int)${input$x},(int)${input$y},(int)${input$z});
	BlockState _bs =  world.getBlockState(_pos);
	Property<?> _property = _bs.getBlock().getStateDefinition().getProperty(${input$property});
	if (_property instanceof BooleanProperty _booleanProp)
		world.setBlock(_pos, _bs.setValue(_booleanProp, ${input$value}), 3);
}