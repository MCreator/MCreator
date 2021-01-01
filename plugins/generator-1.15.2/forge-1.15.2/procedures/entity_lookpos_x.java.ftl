(${input$entity}.world.rayTraceBlocks(new RayTraceContext(${input$entity}.getEyePosition(1f), ${input$entity}.getEyePosition(1f)
        .add(${input$entity}.getLook(1f).x * ${input$maxdistance}, ${input$entity}.getLook(1f).y * ${input$maxdistance}, ${input$entity}.getLook(1f).z * ${input$maxdistance}),
        RayTraceContext.BlockMode.${(field$block_mode?? && field$block_mode != "VISUAL")?then(field$block_mode,"OUTLINE")},
        RayTraceContext.FluidMode.${field$fluid_mode!"NONE"}, ${input$entity})).getPos().getX())