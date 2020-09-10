if (!world.getWorld().isRemote) {
	BlockPos _bp = new BlockPos((int) ${input$x}, (int) ${input$y}, (int) ${input$z});
	TileEntity _tileEntity = world.getTileEntity(_bp);
	BlockState _bs = world.getBlockState(_bp);
	if (_tileEntity != null)
		_tileEntity.getTileData().putDouble(${input$tag}, ((new Object() {
			public double getValue(BlockPos pos, String tag) {
				TileEntity tileEntity = world.getTileEntity(pos);
				if (tileEntity != null)
					return tileEntity.getTileData().getDouble(tag);
				return -1;
			}
		}.getValue(new BlockPos((int) ${input$x}, (int) ${input$y}, (int) ${input$z}), ${input$tag})) + ${input$increase}));
	world.getWorld().notifyBlockUpdate(_bp, _bs, _bs, 3);
}
