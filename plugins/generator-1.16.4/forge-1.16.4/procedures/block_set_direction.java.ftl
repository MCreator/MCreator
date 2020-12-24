try {
	BlockState _bs =  world.getBlockState(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}));
	DirectionProperty _property = (DirectionProperty) _bs.getBlock().getStateContainer().getProperty("facing");
	if (_property != null) {
		world.setBlockState(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}),
			_bs.with(_property, ${input$direction}), 3);
	} else {
		world.setBlockState(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}),
			_bs.with((EnumProperty<Direction.Axis>) _bs.getBlock().getStateContainer().getProperty("axis"),
			${input$direction}.getAxis()), 3);
	}
} catch (Exception e) {
}