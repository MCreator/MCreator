world.getBlockState(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}))
        .getBlock().dropBlockAsItem(world,new BlockPos((int)${input$x2},(int)${input$y2},(int)${input$z2}),
        world.getBlockState(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z})),1);
world.setBlockToAir(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}));