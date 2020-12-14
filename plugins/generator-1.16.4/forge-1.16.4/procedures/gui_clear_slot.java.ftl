if(${input$entity} instanceof ServerPlayerEntity) {
	Container _current = ((ServerPlayerEntity) ${input$entity}).openContainer;
	if(_current instanceof Supplier) {
		Object invobj = ((Supplier) _current).get();
		if(invobj instanceof Map) {
			((Slot) ((Map) invobj).get((int)(${input$slotid}))).putStack(ItemStack.EMPTY);
			_current.detectAndSendChanges();
		}
	}
}