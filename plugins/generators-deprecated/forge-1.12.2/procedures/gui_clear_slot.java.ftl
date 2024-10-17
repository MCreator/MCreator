if(entity instanceof EntityPlayerMP) {
	Container _current = ((EntityPlayerMP) entity).openContainer;
	if(_current instanceof Supplier) {
		Object invobj = ((Supplier) _current).get();
		if(invobj instanceof Map) {
			((Slot) ((Map) invobj).get((int)(${input$slotid}))).putStack(ItemStack.EMPTY);
			_current.detectAndSendChanges();
		}
	}
}