try {
	if (${input$entity} instanceof ServerPlayerEntity) {
		ServerPlayerEntity _ent = (ServerPlayerEntity) ${input$entity};
		_ent.server.getLootTableManager().getLootTableFromLocation(new ResourceLocation(${input$lootTable})).fillInventory((IInventory) _ent.inventory,
			_ent.getLootContextBuilder(true, _ent.getLastDamageSource()).withParameter(LootParameters.BLOCK_STATE, _ent.getServerWorld().getBlockState(new BlockPos(_ent)))
			.withParameter(LootParameters.BLOCK_ENTITY, _ent.getServerWorld().getTileEntity(new BlockPos(_ent))).withParameter(LootParameters.TOOL, _ent.inventory.getCurrentItem())
			.withParameter(LootParameters.EXPLOSION_RADIUS, 0).build(LootParameterSets.GENERIC));
	}
} catch (Exception _e) {
	;
}