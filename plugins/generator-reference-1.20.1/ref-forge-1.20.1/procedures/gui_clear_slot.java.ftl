if(${input$entity} instanceof Player _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
	((Slot) _slots.get(${opt.toInt(input$slotid)})).set(ItemStack.EMPTY);
	_player.containerMenu.broadcastChanges();
}