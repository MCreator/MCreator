(entity.world.rayTraceBlocks(entity.getPositionEyes(1f), entity.getPositionEyes(1f)
        .addVector(entity.getLook(1f).x * ${input$maxdistance}, entity.getLook(1f).y * ${input$maxdistance}, entity.getLook(1f).z * ${input$maxdistance}), false, false, true).getBlockPos().getX())