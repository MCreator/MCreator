if(${input$entity} instanceof Player _player && _player.containerMenu instanceof ${JavaModName}Menus.MenuAccessor _menu) {
	_menu.getSlots().get(${opt.toInt(input$slotid)}).remove(${opt.toInt(input$amount)});
	_player.containerMenu.broadcastChanges();
}