if (world instanceof ServerLevel _level)
	_level.getServer().getCommands().performPrefixedCommand(
	new CommandSourceStack(CommandSource.NULL, new Vec3(1, 1, 1), Vec2.ZERO,
	_level, 4, "", Component.literal(""), _level.getServer(), null).withSuppressedOutput(), "tellraw @a {\"text\":${input$command?json_string}<#if field$color??>, \"color\": \"${field$color}\"</#if><#if (field$bold!"FALSE") == "TRUE">, \"bold\":true</#if><#if (field$italic!"FALSE") == "TRUE">, \"italic\":true</#if><#if (field$underlined!"FALSE") == "TRUE">, \"underlined\":true</#if>}"
);

