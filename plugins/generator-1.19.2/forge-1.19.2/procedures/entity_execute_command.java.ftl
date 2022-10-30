{
	Entity _ent = ${input$entity};
	if(!_ent.level.isClientSide() && _ent.getServer() != null)
		_ent.getServer().getCommands().performPrefixedCommand(_ent.createCommandSourceStack().withSuppressedOutput().withPermission(${input$permissionLevel}), ${input$command});
}