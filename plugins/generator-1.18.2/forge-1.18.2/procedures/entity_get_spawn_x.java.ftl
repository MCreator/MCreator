(new Object() {
	public double getXSpawn(Entity _entity) {
		if (_entity instanceof ServerPlayer _player && !_player.level.isClientSide()) {
			if (_player.getRespawnDimension().equals(_player.level.dimension()) && _player.getRespawnPosition() != null)
				return _player.getRespawnPosition().getX();
			return _player.level.getLevelData().getXSpawn();
		}
		return 0;
	}
}.getXSpawn(${input$entity}))