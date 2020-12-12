<#if field$dimension??><#--Here for legacy reasons as field$dimension does not exist in older workspaces-->
{
	Entity _ent = ${input$entity};
	if(!_ent.world.isRemote&&_ent instanceof ServerPlayerEntity) {
		<#if field$dimension=="Surface">
			RegistryKey<World> destinationType = World.OVERWORLD;
		<#elseif field$dimension=="Nether">
			RegistryKey<World> destinationType = World.THE_NETHER;
		<#elseif field$dimension=="End">
			RegistryKey<World> destinationType = World.THE_END;
		<#else>
			RegistryKey<World> destinationType = RegistryKey.getOrCreateKey(Registry.WORLD_KEY,
				new ResourceLocation("${generator.getResourceLocationForModElement(field$dimension.replace("CUSTOM:", ""))}"));
		</#if>

		ServerWorld nextWorld = _ent.getServer().getWorld(destinationType);
		((ServerPlayerEntity) _ent).connection.sendPacket(new SChangeGameStatePacket(SChangeGameStatePacket.field_241768_e_, 0));
		((ServerPlayerEntity) _ent).teleport(nextWorld, nextWorld.getSpawnPoint().getX(), nextWorld.getSpawnPoint().getY()+1, nextWorld.getSpawnPoint().getZ(), _ent.rotationYaw, _ent.rotationPitch);
		((ServerPlayerEntity) _ent).connection.sendPacket(new SPlayerAbilitiesPacket(((ServerPlayerEntity) _ent).abilities));
		for(EffectInstance effectinstance : ((ServerPlayerEntity) _ent).getActivePotionEffects()) {
			((ServerPlayerEntity) _ent).connection.sendPacket(new SPlayEntityEffectPacket(_ent.getEntityId(), effectinstance));
		}
		((ServerPlayerEntity) _ent).connection.sendPacket(new SPlaySoundEventPacket(1032, BlockPos.ZERO, 0, false));
	}
}
</#if>