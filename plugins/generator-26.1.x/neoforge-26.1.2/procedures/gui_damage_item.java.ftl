if(${input$entity} instanceof Player _player && _player.level() instanceof ServerLevel _serverLevel && _player.containerMenu instanceof ${JavaModName}Menus.MenuAccessor _menu) {
	Slot _slot = _menu.getSlots().get(${opt.toInt(input$slotid)});
	ItemStack stack = _slot.getItem();
	if (stack != null && !stack.isEmpty()) {
		stack.hurtAndBreak(${opt.toInt(input$amount)}, _serverLevel, null, _stkprov -> {});
		_slot.set(stack);
		_slot.setChanged();
		_player.containerMenu.broadcastChanges();
	}
}