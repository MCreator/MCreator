if(${input$entity} instanceof PlayerEntity) {
	Entity _ent = ${input$entity};
    BlockPos _bp = new BlockPos((int)${input$x},(int)${input$y},(int)${input$z});
    _ent.world.getBlockState(_bp).getBlock().onBlockActivated(_ent.world.getBlockState(_bp), _ent.world, _bp, (PlayerEntity) ${input$entity}, Hand.MAIN_HAND,
        BlockRayTraceResult.createMiss(new Vec3d(_bp.getX(), _bp.getY(), _bp.getZ()), Direction.UP, _bp));
}