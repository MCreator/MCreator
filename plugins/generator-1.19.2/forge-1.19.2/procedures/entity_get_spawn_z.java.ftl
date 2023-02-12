(new Object() {
	public double getZSpawn(Entity _entity) {
		Level _level = _entity.level;
		if (_entity.getServer() != null) _level = _entity.getServer().getLevel(_level.dimension());
		if (_entity instanceof ServerPlayer _player && _player.getRespawnDimension().equals(_level.dimension()) && _player.getRespawnPosition() != null)
			return _player.getRespawnPosition().getZ();
		return _level.getLevelData().getZSpawn();
	}
}.getZSpawn(${input$entity}))