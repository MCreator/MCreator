if (world instanceof ServerLevel _level) {
	MutableComponent message = Component.literal(${input$text});

	message.withStyle(style -> style
		.withColor(TextColor.fromRgb(${field$color}))
		.withBold(${field$bold?lower_case})
		.withItalic(${field$italic?lower_case})
		.withUnderlined(${field$underlined?lower_case == "true"})
	);

	_level.getServer().getPlayerList().broadcastSystemMessage(message, false);
}

