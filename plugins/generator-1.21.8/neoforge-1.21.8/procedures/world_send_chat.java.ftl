if (world instanceof ServerLevel _level) {
	MutableComponent message = Component.literal(${input$text!"Message"});

	message.withStyle(style -> style
		.withColor(TextColor.fromRgb(${field$color!16777215}))
		.withBold(${field$bold?lower_case!"false"})
		.withItalic(${field$italic?lower_case!"false"})
		.withUnderlined(${field$underlined?lower_case!"false"})
	);

	_level.getServer().getPlayerList().broadcastSystemMessage(message, false);
}

