if (!world.isClientSide() && world.getServer() != null)
	world.getServer().getPlayerList().broadcastMessage(new TextComponent(${input$text}), ChatType.SYSTEM, Util.NIL_UUID);