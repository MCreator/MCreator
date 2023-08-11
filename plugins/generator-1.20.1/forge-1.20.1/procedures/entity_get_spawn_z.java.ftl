((${input$entity} instanceof ServerPlayer _player && !_player.level().isClientSide()) ?
((_player.getRespawnDimension().equals(_player.level().dimension()) && _player.getRespawnPosition() != null) ?
_player.getRespawnPosition().getZ() : _player.level().getLevelData().getZSpawn()) : 0)