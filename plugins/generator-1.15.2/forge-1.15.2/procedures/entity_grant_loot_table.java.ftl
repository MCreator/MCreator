try {
	if (${input$entity} instanceof ServerPlayerEntity) {
		ServerPlayerEntity _ent = (ServerPlayerEntity) ${input$entity};
		_ent.server.getLootTableManager().getLootTableFromLocation(new ResourceLocation(${input$lootTable})).fillInventory((IInventory) _ent.inventory, (new LootContext.Builder(_ent.getServerWorld())).withParameter(LootParameters.THIS_ENTITY, _ent)
			.withNullableParameter(LootParameters.LAST_DAMAGE_PLAYER, (PlayerEntity) (_ent.getAttackingEntity() instanceof PlayerEntity ? _ent.getAttackingEntity() : null)).withNullableParameter(LootParameters.DAMAGE_SOURCE, _ent.getLastDamageSource())
			.withNullableParameter(LootParameters.KILLER_ENTITY, _ent.getLastDamageSource().getTrueSource()).withNullableParameter(LootParameters.DIRECT_KILLER_ENTITY, _ent.getLastDamageSource().getImmediateSource())
			.withParameter(LootParameters.POSITION, new BlockPos(_ent)).withParameter(LootParameters.BLOCK_STATE, _ent.getServerWorld().getBlockState(new BlockPos(_ent))).withNullableParameter(LootParameters.BLOCK_ENTITY, _ent.getServerWorld().getTileEntity(new BlockPos(_ent)))
			.withParameter(LootParameters.TOOL, _ent.inventory.getCurrentItem()).withParameter(LootParameters.EXPLOSION_RADIUS, 0F).withRandom(_ent.getRNG()).withLuck(_ent.getLuck()).build(LootParameterSets.GENERIC)
);
	}
} catch (Exception _e) {
	;
}