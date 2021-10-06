if(${input$entity} instanceof ServerPlayer _player && _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
	((Slot) _slots.get(${opt.toInt(input$slotid)})).remove(${opt.toInt(input$amount)});
	_player.containerMenu.broadcastChanges();
}