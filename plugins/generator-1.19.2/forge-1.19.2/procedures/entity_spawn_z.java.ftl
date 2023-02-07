(new Object() {
	public double getZSpawn(Entity _entity) {
		if (_entity instanceof ServerPlayer _player && _player.getRespawnDimension().equals(_entity.level.dimension()) && _player.getRespawnPosition() != null)
			return _player.getRespawnPosition().getZ();
		return _entity.level.getLevelData().getZSpawn();
	}
}.getZSpawn(${input$entity}))