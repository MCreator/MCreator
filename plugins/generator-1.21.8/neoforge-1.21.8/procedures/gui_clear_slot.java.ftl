if(${input$entity} instanceof Player _player && _player.containerMenu instanceof ${JavaModName}Menus.MenuAccessor _menu) {
	_menu.getSlots().get(${opt.toInt(input$slotid)}).set(ItemStack.EMPTY);
	_player.containerMenu.broadcastChanges();
}