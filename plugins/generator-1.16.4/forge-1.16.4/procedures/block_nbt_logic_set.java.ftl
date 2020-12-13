if(!world.getWorld().isRemote) {
	BlockPos _bp = new BlockPos((int)${input$x},(int)${input$y},(int)${input$z});
	TileEntity _tileEntity=world.getTileEntity(_bp);
	BlockState _bs = world.getBlockState(_bp);
	if(_tileEntity != null)
		_tileEntity.getTileData().putBoolean(${input$tagName}, ${input$tagValue});

	world.getWorld().notifyBlockUpdate(_bp, _bs, _bs, 3);
}