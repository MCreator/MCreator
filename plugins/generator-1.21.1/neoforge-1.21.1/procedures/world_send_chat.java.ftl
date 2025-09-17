<@head>if (!world.isClientSide() && world.getServer() != null) {</@head>
	world.getServer().getPlayerList().broadcastSystemMessage(Component.literal(${input$text}), false);
<@tail>}</@tail>