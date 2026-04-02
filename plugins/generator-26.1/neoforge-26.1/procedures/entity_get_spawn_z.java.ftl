((${input$entity} instanceof ServerPlayer _player) ?
	(_player.getRespawnConfig() != null && (ServerPlayer.RespawnConfig.getDimensionOrDefault(_player.getRespawnConfig()).equals(_player.level().dimension())) ?
		_player.getRespawnConfig().respawnData().pos().getZ() : _player.level().getLevelData().getRespawnData().pos().getZ()) : 0)