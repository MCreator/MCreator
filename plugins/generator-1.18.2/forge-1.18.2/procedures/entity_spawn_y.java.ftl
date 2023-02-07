(new Object() {
	public double getYSpawn(Entity _entity) {
		if (_entity instanceof ServerPlayer _player && _player.getRespawnDimension().equals(_entity.level.dimension()) && _player.getRespawnPosition() != null)
			return _player.getRespawnPosition().getY();
		return _entity.level.getLevelData().getYSpawn();
	}
}.getYSpawn(${input$entity}))