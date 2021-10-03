if(${input$entity} instanceof ServerPlayer _player) {
	AbstractContainerMenu _current = _player.containerMenu;
	if(_current instanceof Supplier) {
		Object invobj = ((Supplier) _current).get();
		if(invobj instanceof Map) {
			((Slot) ((Map) invobj).get((int)(${input$slotid}))).set(ItemStack.EMPTY);
			_current.broadcastChanges();
		}
	}
}