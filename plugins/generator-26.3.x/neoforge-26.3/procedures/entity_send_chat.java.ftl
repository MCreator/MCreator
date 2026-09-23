if (${input$entity} instanceof ServerPlayer _player)
	_player.sendSystemMessage(Component.literal(${input$text}), ${input$actbar});