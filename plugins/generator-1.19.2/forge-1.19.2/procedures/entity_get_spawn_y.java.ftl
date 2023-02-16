(new Object() {
	public double getYSpawn(Entity _entity) {
		if (_entity instanceof ServerPlayer _player && !_player.level.isClientSide()) {
			if (_player.getRespawnDimension().equals(_player.level.dimension()) && _player.getRespawnPosition() != null)
				return _player.getRespawnPosition().getY();
			return _player.level.getLevelData().getYSpawn();
		}
		return 0;
	}
}.getYSpawn(${input$entity}))