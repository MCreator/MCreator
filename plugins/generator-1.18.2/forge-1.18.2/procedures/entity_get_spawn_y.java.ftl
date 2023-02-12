(new Object() {
	public double getYSpawn(Entity _entity) {
		Level _level = _entity.level;
		if (_entity.getServer() != null) _level = _entity.getServer().getLevel(_level.dimension());
		if (_entity instanceof ServerPlayer _player && _player.getRespawnDimension().equals(_level.dimension()) && _player.getRespawnPosition() != null)
			return _player.getRespawnPosition().getY();
		return _level.getLevelData().getYSpawn();
	}
}.getYSpawn(${input$entity}))