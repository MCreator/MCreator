if(world instanceof Level _level)
    ((${field$dimension.replace("CUSTOM:", "")}PortalBlock) ${JavaModName}Blocks.${generator.getRegistryNameForModElement(field$dimension.replace("CUSTOM:", ""))?upper_case}_PORTAL)
        .portalSpawn(_level, new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}));