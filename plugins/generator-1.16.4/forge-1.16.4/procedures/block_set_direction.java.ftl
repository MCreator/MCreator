try {
	BlockState _bs =  world.getBlockState(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}));
	world.setBlockState(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}),
        _bs.with((DirectionProperty) _bs.getBlock().getStateContainer().getProperty("facing"), ${input$direction}), 3);
} catch (Exception e) {
}