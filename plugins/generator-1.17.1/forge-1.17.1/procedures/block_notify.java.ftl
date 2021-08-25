if(world instanceof Level level)
    level.updateNeighborsAt(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}),
        level.getBlockState(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z})).getBlock());