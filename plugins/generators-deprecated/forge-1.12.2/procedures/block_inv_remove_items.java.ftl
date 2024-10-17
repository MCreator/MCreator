{
	TileEntity inv=world.getTileEntity(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}));
	if(inv instanceof TileEntityLockableLoot)
		((TileEntityLockableLoot)inv).decrStackSize((int)(${input$slotid}),(int)(${input$amount}));
}