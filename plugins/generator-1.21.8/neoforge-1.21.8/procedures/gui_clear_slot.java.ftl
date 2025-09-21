<@head>if(${input$entity} instanceof Player _player) {</@head>
	if (_player.containerMenu instanceof ${JavaModName}Menus.MenuAccessor _menu){
		_menu.getSlots().get(${opt.toInt(input$slotid)}).set(ItemStack.EMPTY);
	}
<@tail>
	_player.containerMenu.broadcastChanges();
}
</@tail>