if(${input$entity} instanceof PlayerEntity && world instanceof World) {
    BlockPos _bp = new BlockPos((int)${input$x},(int)${input$y},(int)${input$z});
    world.getBlockState(_bp).getBlock().onBlockActivated(world.getBlockState(_bp), world.getWorld(), _bp, (PlayerEntity) ${input$entity}, Hand.MAIN_HAND,
        BlockRayTraceResult.createMiss(new Vec3d(_bp.getX(), _bp.getY(), _bp.getZ()), Direction.UP, _bp));
}