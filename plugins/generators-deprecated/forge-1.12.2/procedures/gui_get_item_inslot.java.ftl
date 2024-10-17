/*@ItemStack*/(new Object(){
	public ItemStack getItemStack(int sltid) {
		if(entity instanceof EntityPlayerMP) {
		Container _current = ((EntityPlayerMP) entity).openContainer;
		if(_current instanceof Supplier) {
		Object invobj = ((Supplier) _current).get();
		if(invobj instanceof Map) {
		return ((Slot) ((Map) invobj).get(sltid)).getStack();
		}
		}
		}
		return ItemStack.EMPTY;
		}
		}.getItemStack((int)(${input$slotid})))