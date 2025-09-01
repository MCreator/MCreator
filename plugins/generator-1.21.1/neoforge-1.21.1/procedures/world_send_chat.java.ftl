if (world instanceof ServerLevel _level) {
	MutableComponent message = Component.literal(${input$text!"Message"});

	<#if field$color?? && field$bold?? && field$italic?? && field$underlined??>
		message.withStyle(style -> style
			.withColor(TextColor.fromRgb(${field$color!16777215}))
			.withBold(${(field$bold!"false")?lower_case == "true"})
			.withItalic(${(field$italic!"false")?lower_case == "true"})
			.withUnderlined(${(field$underlined!"false")?lower_case == "true"})
		);
	</#if>

	_level.getServer().getPlayerList().broadcastSystemMessage(message, false);
}

