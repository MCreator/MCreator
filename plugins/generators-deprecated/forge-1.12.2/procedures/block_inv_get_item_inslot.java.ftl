/*@ItemStack*/(new Object(){
	public ItemStack getItemStack(BlockPos pos,int sltid){
		TileEntity inv=world.getTileEntity(pos);
		if(inv instanceof TileEntityLockableLoot)
		return((TileEntityLockableLoot)inv).getStackInSlot(sltid);
		return ItemStack.EMPTY;
		}
		}.getItemStack(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}),(int)(${input$slotid})))