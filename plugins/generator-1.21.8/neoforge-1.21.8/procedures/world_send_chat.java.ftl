<#assign colorMap = {
	"white": "0xFFFFFF",
	"gray": "0xAAAAAA",
	"dark gray": "0x555555",
	"black": "0x000000",
	"red": "0xFF5555",
	"dark red": "0xAA0000",
	 "blue": "0x5555FF",
	"dark blue": "0x0000AA",
	"green": "0x55FF55",
	"dark green": "0x00AA00",
	"yellow": "0xFFFF55",
	"gold": "0xFFAA00"
}>
<#assign colorRGB = colorMap[field$color]! "0xFFFFFF">

if (world instanceof ServerLevel _level) {
	MutableComponent message = Component.literal(${input$text});

	message.withStyle(style -> style
		.withColor(TextColor.fromRgb(${colorRGB}))
		.withBold(${field$bold?lower_case})
		.withItalic(${field$italic?lower_case})
		.withUnderlined(${field$underlined?lower_case == "true"})
	);

	_level.getServer().getPlayerList().broadcastSystemMessage(message, false);
}

