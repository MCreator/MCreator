((${input$entity} instanceof ServerPlayer _player && !_player.level().isClientSide()) ?
((_player.getRespawnDimension().equals(_player.level().dimension()) && _player.getRespawnPosition() != null) ?
_player.getRespawnPosition().getX() : _player.level().getLevelData().getXSpawn()) : 0)