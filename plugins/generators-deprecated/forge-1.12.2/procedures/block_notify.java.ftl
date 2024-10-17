world.notifyNeighborsOfStateChange(
		new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}),
        world.getBlockState(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z})).getBlock(),
        true);