{
	Entity _ent = ${input$entity};
	if(!_ent.world.isRemote && _ent.world.getServer() != null) {
		_ent.world.getServer().getCommandManager().handleCommand(_ent.getCommandSource()
			.withFeedbackDisabled().withPermissionLevel(4), ${input$command});
	}
}