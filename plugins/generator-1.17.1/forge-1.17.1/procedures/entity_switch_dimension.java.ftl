<#if field$dimension??><#--Here for legacy reasons as field$dimension does not exist in older workspaces-->
if(${input$entity} instanceof ServerPlayer _player && !_player.level.isClientSide()) {
	<#if field$dimension=="Surface">
		ResourceKey<Level> destinationType = Level.OVERWORLD;
	<#elseif field$dimension=="Nether">
		ResourceKey<Level> destinationType = Level.NETHER;
	<#elseif field$dimension=="End">
		ResourceKey<Level> destinationType = Level.END;
	<#else>
		ResourceKey<Level> destinationType = ResourceKey.create(Registry.DIMENSION_REGISTRY,
			new ResourceLocation("${generator.getResourceLocationForModElement(field$dimension.replace("CUSTOM:", ""))}"));
	</#if>
	if (_player.level.dimension() == destinationType) return;

	ServerLevel nextLevel = _player.server.getLevel(destinationType);
	if (nextLevel != null) {
		_player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, 0));
		_player.teleportTo(nextLevel, nextLevel.getSharedSpawnPos().getX(), nextLevel.getSharedSpawnPos().getY()+1, nextLevel.getSharedSpawnPos().getZ(), _player.getYRot(), _player.getXRot());
		_player.connection.send(new ClientboundPlayerAbilitiesPacket(_player.getAbilities()));
		for(MobEffectInstance effectinstance : _player.getActiveEffects())
			_player.connection.send(new ClientboundUpdateMobEffectPacket(_player.getId(), effectinstance));
		_player.connection.send(new ClientboundLevelEventPacket(1032, BlockPos.ZERO, 0, false));
	}
}
</#if>