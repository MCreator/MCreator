if (world instanceof ServerLevel _level)
	_level.getServer().getCommands().performPrefixedCommand(
	new CommandSourceStack(CommandSource.NULL, new Vec3(${input$x}, ${input$y}, ${input$z}), Vec2.ZERO,
	_level, 4, "", Component.literal(""), _level.getServer(), null).withSuppressedOutput(), ${input$command});