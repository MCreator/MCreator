if(${input$entity} instanceof Player _player && _player.level() instanceof ServerLevel _serverLevel
		&& _player.containerMenu instanceof Supplier _current && _current.get() instanceof Map _slots) {
	ItemStack stack = ((Slot) _slots.get(${opt.toInt(input$slotid)})).getItem();
	if(stack != null) {
		stack.hurtAndBreak(${opt.toInt(input$amount)}, _serverLevel, null, _stkprov -> {});
		_player.containerMenu.broadcastChanges();
	}
}