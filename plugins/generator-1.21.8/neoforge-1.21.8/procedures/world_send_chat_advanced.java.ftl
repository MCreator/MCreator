<#if field$color == "white">
  <#assign colorRGB = "0xFFFFFF">
<#elseif field$color == "gray">
  <#assign colorRGB = "0xAAAAAA">
<#elseif field$color == "dark gray">
  <#assign colorRGB = "0x555555">
<#elseif field$color == "black">
  <#assign colorRGB = "0x000000">
<#elseif field$color == "red">
  <#assign colorRGB = "0xFF5555">
<#elseif field$color == "dark red">
  <#assign colorRGB = "0xAA0000">
<#elseif field$color == "blue">
  <#assign colorRGB = "0x5555FF">
<#elseif field$color == "dark blue">
  <#assign colorRGB = "0x0000AA">
<#elseif field$color == "green">
  <#assign colorRGB = "0x55FF55">
<#elseif field$color == "dark green">
  <#assign colorRGB = "0x00AA00">
<#elseif field$color == "yellow">
  <#assign colorRGB = "0xFFFF55">
<#elseif field$color == "gold">
  <#assign colorRGB = "0xFFAA00">
<#else>
  <#assign colorRGB = "0xFFFFFF">
</#if>



if (world instanceof ServerLevel _level) {
	MutableComponent message = Component.literal(${input$command});

	message.withStyle(style -> style
		.withColor(TextColor.fromRgb(${colorRGB}))
		.withBold(${field$bold?lower_case})
		.withItalic(${field$italic?lower_case})
		.withUnderlined(${field$underlined?lower_case == "true"})
	);

	_level.getServer().getPlayerList().broadcastSystemMessage(message, false);
}

