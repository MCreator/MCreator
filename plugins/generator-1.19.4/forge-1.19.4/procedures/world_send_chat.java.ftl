if (!world.isClientSide() && world.getServer() != null)
	world.getServer().getPlayerList().broadcastSystemMessage(Component.literal(${input$text}), false);