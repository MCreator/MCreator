if (${input$entity} instanceof Player _player && !_player.level().isClientSide())
	_player.displayClientMessage(Component.literal(${input$text}), ${input$actbar});