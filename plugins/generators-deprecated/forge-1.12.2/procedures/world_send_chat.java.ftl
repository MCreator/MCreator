{
	MinecraftServer mcserv=FMLCommonHandler.instance().getMinecraftServerInstance();
	if(mcserv!=null)
		mcserv.getPlayerList().sendMessage(new TextComponentString(${input$text}));
}