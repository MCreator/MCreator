Minecraft.getInstance().displayGuiScreen(new ConfirmOpenLinkScreen((open) -> {
    if (open) {
    }
	Util.getOSType().openURI("${url}");
}