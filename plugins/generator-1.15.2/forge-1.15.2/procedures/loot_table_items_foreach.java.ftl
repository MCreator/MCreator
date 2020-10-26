if (${input$entity} instanceof LivingEntity) {
	LivingEntity _ent = (LivingEntity) ${input$entity};
	MinecraftServer _server = ServerLifecycleHooks.getCurrentServer();
	ResourceLocation _rl = ResourceLocation.tryCreate(${input$lootTable});
	for (ItemStack itemstackiterator : _server.getLootTableManager().getLootTableFromLocation(_rl != null ? _rl : LootTables.EMPTY).generate((new LootContext.Builder(_server))
			.withParameter(LootParameters.THIS_ENTITY, _ent).withNullableParameter(LootParameters.LAST_DAMAGE_PLAYER, (PlayerEntity) (_ent.getAttackingEntity() instanceof PlayerEntity ? _ent.getAttackingEntity() : null))
			.withNullableParameter(LootParameters.DAMAGE_SOURCE, _ent.getLastDamageSource()).withNullableParameter(LootParameters.KILLER_ENTITY, _ent.getLastDamageSource().getTrueSource())
			.withNullableParameter(LootParameters.DIRECT_KILLER_ENTITY, _ent.getLastDamageSource().getImmediateSource()).withParameter(LootParameters.POSITION, _ent.getPosition()))
			.withParameter(LootParameters.BLOCK_STATE, _ent.world.getBlockState(_ent.getPosition())).withNullableParameter(LootParameters.BLOCK_ENTITY, _ent.world.getTileEntity(_ent.getPosition()))
			.withParameter(LootParameters.TOOL, _ent instanceof PlayerEntity ? ((PlayerEntity) _ent).inventory.getCurrentItem() : _ent.getActiveItemStack()).withParameter(LootParameters.EXPLOSION_RADIUS, 0F)
			.withRandom(_ent.getRNG()).withLuck(_ent.getLuck()).build(LootParameterSets.GENERIC))) {
		${statement$foreach}
	}
}