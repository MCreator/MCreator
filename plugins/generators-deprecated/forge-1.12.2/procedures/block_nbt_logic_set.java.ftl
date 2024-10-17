if(!world.isRemote) {
	BlockPos _bp = new BlockPos((int)${input$x},(int)${input$y},(int)${input$z});
	TileEntity _tileEntity=world.getTileEntity(_bp);
	IBlockState _bs = world.getBlockState(_bp);
	if(_tileEntity!=null)
		_tileEntity.getTileData().setBoolean(${input$tagName}, ${input$tagValue});
	world.notifyBlockUpdate(_bp, _bs, _bs, 3);
}