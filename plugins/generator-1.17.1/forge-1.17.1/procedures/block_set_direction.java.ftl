{
    BlockPos _pos = new BlockPos((int)${input$x},(int)${input$y},(int)${input$z});
	BlockState _bs =  world.getBlockState(_pos);
	Property<?> _property = _bs.getBlock().getStateDefinition().getProperty("facing");
	if (_property != null) {
		world.setBlock(_pos, _bs.setValue((DirectionProperty) _property, ${input$direction}), 3);
	} else {
	    _property = _bs.getBlock().getStateDefinition().getProperty("axis");
	    if (_property != null)
			world.setBlock(_pos, _bs.setValue((EnumProperty<Direction.Axis>) _property, ${input$direction}.getAxis()), 3);
	}
}