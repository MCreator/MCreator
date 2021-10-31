if(world instanceof Level _level)
    ${field$dimension.replace("CUSTOM:", "")}PortalBlock.portalSpawn(_level, new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}));