if(world instanceof Level _level)
    _level.updateNeighborsAt(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}),
        _level.getBlockState(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z})).getBlock());