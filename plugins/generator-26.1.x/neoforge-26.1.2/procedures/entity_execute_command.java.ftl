{
	Entity _ent = ${input$entity};
	if(!_ent.level().isClientSide() && _ent.level().getServer() != null) {
		_ent.level().getServer().getCommands().performPrefixedCommand(new CommandSourceStack(
			CommandSource.NULL, _ent.position(), _ent.getRotationVector(),
			_ent.level() instanceof ServerLevel ? (ServerLevel) _ent.level() : null, LevelBasedPermissionSet.OWNER,
			_ent.getName().getString(), _ent.getDisplayName(), _ent.level().getServer(), _ent
		), ${input$command});
	}
}