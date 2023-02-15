(new Object() {
	public double getXSpawn(Entity _entity) {
		if (_entity instanceof ServerPlayer _player) {
			if (_player.getRespawnDimension().equals(_entity.level.dimension()) && _player.getRespawnPosition() != null)
				return _player.getRespawnPosition().getX();
			return _entity.level.getLevelData().getXSpawn();
		}
		return 0;
	}
}.getXSpawn(${input$entity}))