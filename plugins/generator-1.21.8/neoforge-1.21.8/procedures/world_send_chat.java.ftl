if (world instanceof ServerLevel _level) {
	MutableComponent message = Component.literal(${input$text!"Message"});
	<#if
	   (field$color!"#ffffff")?substring(1) != "ffffff"
	|| (field$bold!"false")?lower_case == "true"
	|| (field$italic!"false")?lower_case == "true"
	|| (field$underlined!"false")?lower_case == "true"
	>
	message.withStyle(style -> style
		<#if (field$color!"#ffffff")?substring(1) != "ffffff">.withColor(TextColor.fromRgb(0x${(field$color!"#ffffff")?substring(1)}))</#if>
		<#if (field$bold!"false")?lower_case == "true">.withBold(true)</#if>
		<#if (field$italic!"false")?lower_case == "true">.withItalic(true)</#if>
		<#if (field$underlined!"false")?lower_case == "true">.withUnderlined(true)</#if>
	);
	</#if>
	_level.getServer().getPlayerList().broadcastSystemMessage(message, false);
}

