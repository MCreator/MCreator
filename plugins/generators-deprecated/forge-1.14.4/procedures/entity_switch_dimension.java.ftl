<#if field$dimension??><#--Here for legacy reasons as field$dimension does not exist in older workspaces-->
{
	Entity _ent = ${input$entity};
	if(!_ent.world.isRemote&&_ent instanceof ServerPlayerEntity) {
		<#if field$dimension=="Surface">
			DimensionType destinationType = DimensionType.OVERWORLD;
		<#elseif field$dimension=="Nether">
			DimensionType destinationType = DimensionType.THE_NETHER;
		<#elseif field$dimension=="End">
			DimensionType destinationType = DimensionType.THE_END;
		<#else>
			DimensionType destinationType = ${(field$dimension.toString().replace("CUSTOM:", ""))}Dimension.type;
		</#if>

		ObfuscationReflectionHelper.setPrivateValue(ServerPlayerEntity.class, (ServerPlayerEntity) _ent, true, "field_184851_cj");

		ServerWorld nextWorld = _ent.getServer().getWorld(destinationType);

		((ServerPlayerEntity) _ent).connection.sendPacket(new SChangeGameStatePacket(4, 0));

		((ServerPlayerEntity) _ent).teleport(nextWorld, nextWorld.getSpawnPoint().getX(), nextWorld.getSpawnPoint().getY()+1, nextWorld.getSpawnPoint().getZ(), _ent.rotationYaw, _ent.rotationPitch);

		((ServerPlayerEntity) _ent).connection.sendPacket(new SPlayerAbilitiesPacket(((ServerPlayerEntity) _ent).abilities));
		for(EffectInstance effectinstance : ((ServerPlayerEntity) _ent).getActivePotionEffects()) {
			((ServerPlayerEntity) _ent).connection.sendPacket(new SPlayEntityEffectPacket(_ent.getEntityId(), effectinstance));
		}
		((ServerPlayerEntity) _ent).connection.sendPacket(new SPlaySoundEventPacket(1032, BlockPos.ZERO, 0, false));
	}
}
</#if>