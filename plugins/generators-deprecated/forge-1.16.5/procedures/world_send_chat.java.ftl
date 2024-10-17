if (!world.isRemote()) {
	MinecraftServer mcserv = ServerLifecycleHooks.getCurrentServer();
	if(mcserv!=null)
		mcserv.getPlayerList().func_232641_a_(new StringTextComponent(${input$text}), ChatType.SYSTEM, Util.DUMMY_UUID);
}