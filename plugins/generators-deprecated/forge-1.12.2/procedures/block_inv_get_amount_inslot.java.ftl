(new Object(){
	public int getAmount(BlockPos pos,int sltid){
		TileEntity inv=world.getTileEntity(pos);
		if(inv instanceof TileEntityLockableLoot){
		ItemStack stack=((TileEntityLockableLoot)inv).getStackInSlot(sltid);
		if(stack!=null)
		return stack.getCount();
		}
		return 0;
		}
		}.getAmount(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}),(int)(${input$slotid})))