try {
	BlockState _bs =  world.getBlockState(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}));
	world.setBlockState(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}),
		_bs.with((IntegerProperty) _bs.getBlock().getStateContainer().getProperty(${input$property}),
			(int) ${input$value}), 3);
} catch (Exception e) {
}