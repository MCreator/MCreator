try {
	Entity _ent = ${input$entity};
	if (_ent instanceof ServerPlayerEntity) {
		_ent.server.getLootTableManager().getLootTableFromLocation(new ResourceLocation(${input$lootTable})).fillInventory((IInventory) _ent.inventory,
			new LootContext.Builder(_ent.getServerWorld()).withParameter(LootParameters.THIS_ENTITY, _ent).withParameter(LootParameters.POSITION, new BlockPos(_ent))
			.withRandom(_ent.getRNG()).withLuck(_ent.getLuck()).build(LootParameterSets.ADVANCEMENT)))
	}
} catch (Exception _e) {
	;
}