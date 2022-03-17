if(world instanceof Level) {
    Block.dropResources(world.getBlockState(
        new BlockPos((int)${input$x},(int)${input$y},(int)${input$z})), (Level) world, new BlockPos((int)${input$x2},(int)${input$y2},(int)${input$z2}));

    world.destroyBlock(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}), false);
}