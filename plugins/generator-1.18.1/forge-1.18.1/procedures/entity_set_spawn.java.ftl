if (${input$entity} instanceof ServerPlayer _serverPlayer)
	_serverPlayer.setRespawnPosition(_serverPlayer.level.dimension(),
		new BlockPos((int) ${input$x}, (int) ${input$y}, (int) ${input$z}), _serverPlayer.getYRot(), true, false);