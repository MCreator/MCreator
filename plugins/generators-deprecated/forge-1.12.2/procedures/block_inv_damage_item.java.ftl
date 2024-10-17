{
	TileEntity inv=world.getTileEntity(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}));
    if(inv!=null&&(inv instanceof TileEntityLockableLoot)){
    	ItemStack stack=((TileEntityLockableLoot)inv).getStackInSlot((int)(${input$slotid}));
    	if(stack!=null){
    		if(stack.attemptDamageItem((int) ${input$amount},new Random(),null)){
    			stack.shrink(1);
    			stack.setItemDamage(0);
			}
    	}
    }
}