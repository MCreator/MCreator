((${input$entity} instanceof ServerPlayer _player) ?
	(_player.getRespawnConfig() != null && (_player.getRespawnConfig().dimension().equals(_player.level().dimension())) ?
		_player.getRespawnConfig().pos().getZ() : _player.level().getLevelData().getSpawnPos().getZ()) : 0)