if(${input$entity} instanceof Player _player && !_player.level.isClientSide())
	_player.displayClientMessage(new TextComponent(${input$text}), ${input$actbar});