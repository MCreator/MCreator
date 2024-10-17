try {
	IBlockState _bs =  world.getBlockState(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}));
	for (IProperty<?> prop : _bs.getProperties().keySet()) {
		if (prop.getName().equals("facing")) {
			world.setBlockState(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}), _bs.withProperty((PropertyDirection) prop, ${input$direction}), 3);
			break;
		}
	}
} catch (Exception e) {
}