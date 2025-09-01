if (world instanceof ServerLevel _level) {
	MutableComponent message = Component.literal(${input$text!"Message"});

	message.withStyle(style -> style
		.withColor(TextColor.fromRgb(${field$color!16777215}))
		.withBold(${(field$bold!"false")?lower_case == "true"})
		.withItalic(${(field$italic!"false")?lower_case == "true"})
		.withUnderlined(${(field$underlined!"false")?lower_case == "true"})
	);

	_level.getServer().getPlayerList().broadcastSystemMessage(message, false);
}

