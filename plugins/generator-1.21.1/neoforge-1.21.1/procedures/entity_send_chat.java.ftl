<@head>if (${input$entity} instanceof Player _player && !_player.level().isClientSide()) {</@head>
	_player.displayClientMessage(Component.literal(${input$text}), ${input$actbar});
<@tail>}</@tail>