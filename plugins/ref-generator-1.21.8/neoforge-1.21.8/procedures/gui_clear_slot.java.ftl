<@head>if (${input$entity} instanceof Player _player && _player.containerMenu instanceof ${JavaModName}Menus.MenuAccessor _menu) {</@head>
	_menu.getSlots().get(${opt.toInt(input$slotid)}).set(ItemStack.EMPTY);
<@tail>
	_player.containerMenu.broadcastChanges();
}</@tail>