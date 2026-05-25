<@head>if (${input$entity} instanceof Player _player && _player.containerMenu instanceof ${JavaModName}Menus.MenuAccessor _menu) {</@head>
	_menu.getSlots().get(${opt.toInt(input$slotid)}).remove(${opt.toInt(input$amount)});
<@tail>
	_player.containerMenu.broadcastChanges();
}</@tail>