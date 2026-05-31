if (world instanceof ServerLevel _level) {
	_level.getServer().getPlayerList().broadcastSystemMessage(Component.literal(${input$text})
		<#if (field$color!"#ffffff")?substring(1) != "ffffff">.withColor(0x${(field$color!"#ffffff")?substring(1)})</#if>
		<#if (field$bold!"false")?lower_case == "true">.withStyle(ChatFormatting.BOLD)</#if>
		<#if (field$italic!"false")?lower_case == "true">.withStyle(ChatFormatting.ITALIC)</#if>
		<#if (field$underlined!"false")?lower_case == "true">.withStyle(ChatFormatting.UNDERLINE)</#if>
	, false);
}