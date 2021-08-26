{
	Direction _dir = ${input$direction};
	BlockPos _pos = new BlockPos((int)${input$x},(int)${input$y},(int)${input$z});
	BlockState _bs =  world.getBlockState(_pos);
	Property<?> _property = _bs.getBlock().getStateDefinition().getProperty("facing");
	if (_property instanceof DirectionProperty _dp && _dp.getPossibleValues().contains(_dir)) {
		world.setBlock(_pos, _bs.setValue(_dp, _dir), 3);
	} else {
		_property = _bs.getBlock().getStateDefinition().getProperty("axis");
		if (_property != null)
			world.setBlock(_pos, _bs.setValue((EnumProperty<Direction.Axis>) _property, _dir.getAxis()), 3);
	}
}