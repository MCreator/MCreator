@SubscribeEvent public void onPlayerPressesKey(GuiScreenEvent.KeyboardKeyPressedEvent event) {
	int keyCode = event.getKeyCode();
	Map<String, Object> dependencies = new HashMap<>();
	dependencies.put("keyCode",keyCode);
	dependencies.put("event",event);
	this.executeProcedure(dependencies);
}