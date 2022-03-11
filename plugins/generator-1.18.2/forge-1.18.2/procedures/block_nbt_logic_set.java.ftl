if(!world.isClientSide()) {
	BlockPos _bp = new BlockPos((int)${input$x},(int)${input$y},(int)${input$z});
	BlockEntity _blockEntity = world.getBlockEntity(_bp);
	BlockState _bs = world.getBlockState(_bp);
	if(_blockEntity != null)
		_blockEntity.getTileData().putBoolean(${input$tagName}, ${input$tagValue});

	if(world instanceof Level _level)
		_level.sendBlockUpdated(_bp, _bs, _bs, 3);
}