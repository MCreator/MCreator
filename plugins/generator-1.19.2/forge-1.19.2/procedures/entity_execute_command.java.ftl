{
	Entity _ent = ${input$entity};
	if(!_ent.level.isClientSide() && _ent.getServer() != null) {
		CommandSourceStack _css = new CommandSourceStack(
			CommandSource.NULL <#-- fix #3257 for 43.1.3+ -->, _ent.position(), _ent.getRotationVector(),
			_ent.level instanceof ServerLevel ? (ServerLevel) _ent.level : null, 4,
			_ent.getName().getString(), _ent.getDisplayName(), _ent.level.getServer(), _ent
		) {
			<#-- fix #3257 for 43.1.1, this fix will not be needed on FG 44.x.x+ as CommandSource.NULL fix above is enough -->
			@Override @Nullable public Entity getEntity() {
				if (StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass() == ForgeHooks.class)
					return null; <#-- We hide the fact that we have commands source entity for ForgeHooks to bypass PermissionAPI (bug #3257) -->
				return super.getEntity();
			}
		};
		<#-- do not call .with methods on _css below or getEntity() will be overwritten with default -->
		_ent.getServer().getCommands().performPrefixedCommand(_css, ${input$command});
	}
}