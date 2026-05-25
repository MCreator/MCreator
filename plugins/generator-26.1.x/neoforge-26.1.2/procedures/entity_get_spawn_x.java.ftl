((${input$entity} instanceof ServerPlayer _player) ?
	(_player.getRespawnConfig() != null && (ServerPlayer.RespawnConfig.getDimensionOrDefault(_player.getRespawnConfig()).equals(_player.level().dimension())) ?
		_player.getRespawnConfig().respawnData().pos().getX() : _player.level().getLevelData().getRespawnData().pos().getX()) : 0)