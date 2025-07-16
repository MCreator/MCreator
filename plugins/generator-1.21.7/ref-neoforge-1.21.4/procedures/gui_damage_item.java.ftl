if(${input$entity} instanceof Player _player && _player.level() instanceof ServerLevel _serverLevel && _player.containerMenu instanceof ${JavaModName}Menus.MenuAccessor _menu) {
	ItemStack stack = _menu.getSlots().get(${opt.toInt(input$slotid)}).getItem();
	if(stack != null) {
		stack.hurtAndBreak(${opt.toInt(input$amount)}, _serverLevel, null, _stkprov -> {});
		_player.containerMenu.broadcastChanges();
	}
}