if (world instanceof ServerLevel _level) {
	MutableComponent message = Component.literal(${input$text!"Message"});
	<#assign newcolor = field$color?substring(1)>
	message.withStyle(style -> style
		.withColor(TextColor.fromRgb(0x${newcolor}))
		.withBold(${(field$bold!"false")?lower_case == "true"})
		.withItalic(${(field$italic!"false")?lower_case == "true"})
		.withUnderlined(${(field$underlined!"false")?lower_case == "true"})
	);
	_level.getServer().getPlayerList().broadcastSystemMessage(message, false);
}

