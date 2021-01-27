<#-- @formatter:off -->
if (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER) {
	final Screen _prevscr = Minecraft.getInstance().currentScreen;
	Minecraft.getInstance().displayGuiScreen(new ConfirmOpenLinkScreen(open -> {
		if (open)
			Util.getOSType().openURI(${input$url});
		Minecraft.getInstance().displayGuiScreen(_prevscr);
	}, ${input$url}, true));
}
<#-- @formatter:on -->