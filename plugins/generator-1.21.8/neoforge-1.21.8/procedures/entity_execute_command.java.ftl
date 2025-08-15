{
	Entity _ent = ${input$entity};
	if(!_ent.level().isClientSide() && _ent.getServer() != null) {
		_ent.getServer().getCommands().performPrefixedCommand(new CommandSourceStack(
			CommandSource.NULL, _ent.position(), _ent.getRotationVector(),
			_ent.level() instanceof ServerLevel ? (ServerLevel) _ent.level() : null, 4,
			_ent.getName().getString(), _ent.getDisplayName(), _ent.level().getServer(), _ent
		), ${input$command});
	}
}