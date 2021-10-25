if (!world.isClientSide()) {
	MinecraftServer mcserv = ServerLifecycleHooks.getCurrentServer();
	if(mcserv!=null)
		mcserv.getPlayerList().broadcastMessage(new TextComponent(${input$text}), ChatType.SYSTEM, Util.NIL_UUID);
}