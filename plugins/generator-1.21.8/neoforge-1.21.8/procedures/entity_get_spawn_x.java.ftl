((${input$entity} instanceof ServerPlayer _player) ?
	(_player.getRespawnConfig() != null && (_player.getRespawnConfig().dimension().equals(_player.level().dimension())) ?
		_player.getRespawnConfig().pos().getX() : _player.level().getLevelData().getSpawnPos().getX()) : 0)