{
	MinecraftServer mcserv = ServerLifecycleHooks.getCurrentServer();
	if(mcserv!=null)
		mcserv.getPlayerList().sendMessage(new StringTextComponent(${input$text}));
}