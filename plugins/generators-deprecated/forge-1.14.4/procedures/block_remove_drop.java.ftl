Block.spawnDrops(world.getBlockState(
		new BlockPos((int)${input$x},(int)${input$y},(int)${input$z})), world.getWorld(), new BlockPos((int)${input$x2},(int)${input$y2},(int)${input$z2}));
world.destroyBlock(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}), false);