if(entity instanceof EntityPlayer) {
    BlockPos _bp = new BlockPos((int)${input$x},(int)${input$y},(int)${input$z});
    world.getBlockState(_bp).getBlock().onBlockActivated(world, _bp, world.getBlockState(_bp), (EntityPlayer) entity, EnumHand.MAIN_HAND,
        EnumFacing.UP, _bp.getX(), _bp.getY(), _bp.getZ());
}