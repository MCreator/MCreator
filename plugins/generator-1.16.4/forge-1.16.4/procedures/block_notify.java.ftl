if(world instanceof World)
    ((World) world).notifyNeighborsOfStateChange(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}),
        ((World) world).getBlockState(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z})).getBlock());