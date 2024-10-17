if (!world.isClientSide()) {
	MinecraftServer _mcserv = ServerLifecycleHooks.getCurrentServer();
	if (_mcserv != null)
		_mcserv.getPlayerList().broadcastSystemMessage(Component.literal(${input$text}), false);
}