<#-- @formatter:off -->
if (FMLEnvironment.dist == Dist.CLIENT) {
	Minecraft.getInstance().displayGuiScreen(new ConfirmOpenLinkScreen(open -> {
		if (open)
			Util.getOSType().openURI(${input$url});
	}, ${input$url}, true));
}
<#-- @formatter:on -->