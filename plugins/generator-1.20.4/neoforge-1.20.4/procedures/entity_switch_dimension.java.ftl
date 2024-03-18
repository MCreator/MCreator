<#if field$dimension??><#--Here for legacy reasons as field$dimension does not exist in older workspaces-->
if (${input$entity} instanceof ServerPlayer _player && !_player.level().isClientSide()) {
	ResourceKey<Level> destinationType = ${generator.map(field$dimension, "dimensions")};

	if (_player.level().dimension() == destinationType) return;

	ServerLevel nextLevel = _player.server.getLevel(destinationType);
	if (nextLevel != null) {
		_player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, 0));
		_player.teleportTo(nextLevel, _player.getX(), _player.getY(), _player.getZ(), _player.getYRot(), _player.getXRot());
		_player.connection.send(new ClientboundPlayerAbilitiesPacket(_player.getAbilities()));
		for (MobEffectInstance _effectinstance : _player.getActiveEffects())
			_player.connection.send(new ClientboundUpdateMobEffectPacket(_player.getId(), _effectinstance));
		_player.connection.send(new ClientboundLevelEventPacket(1032, BlockPos.ZERO, 0, false));
	}
}
</#if>