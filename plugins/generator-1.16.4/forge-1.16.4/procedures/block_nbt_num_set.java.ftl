if(!((World) world).isRemote) {
	BlockPos _bp = new BlockPos((int)${input$x},(int)${input$y},(int)${input$z});
	TileEntity _tileEntity=world.getTileEntity(_bp);
	BlockState _bs = world.getBlockState(_bp);
	if(_tileEntity!=null)
		_tileEntity.getTileData().putDouble(${input$tagName}, ${input$tagValue});

	((World) world).notifyBlockUpdate(_bp, _bs, _bs, 3);
}