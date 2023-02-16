(new Object() {
	public double getZSpawn(Entity _entity) {
		if (_entity instanceof ServerPlayer _player && !_player.level.isClientSide()) {
			if (_player.getRespawnDimension().equals(_player.level.dimension()) && _player.getRespawnPosition() != null)
				return _player.getRespawnPosition().getZ();
			return _player.level.getLevelData().getZSpawn();
		}
		return 0;
	}
}.getZSpawn(${input$entity}))