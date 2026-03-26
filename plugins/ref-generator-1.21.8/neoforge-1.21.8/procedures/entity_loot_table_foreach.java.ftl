<#include "mcelements.ftl">
<#-- @formatter:off -->
if (${input$entity} instanceof LivingEntity _entLootTbl && _entLootTbl.getServer() != null) {
	DamageSource _dsLootTbl = _entLootTbl.getLastDamageSource();
	if (_dsLootTbl == null) _dsLootTbl = _entLootTbl.damageSources().generic();
	for (ItemStack itemstackiterator : _entLootTbl.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, ${toResourceLocation(input$location)}))
			.getRandomItems(new LootParams.Builder((ServerLevel) _entLootTbl.level())
					.withParameter(LootContextParams.THIS_ENTITY, _entLootTbl)
					.withOptionalParameter(LootContextParams.LAST_DAMAGE_PLAYER, _entLootTbl.getLastHurtByMob() instanceof Player _player ?  _player : null)
					.withParameter(LootContextParams.DAMAGE_SOURCE, _dsLootTbl)
					.withOptionalParameter(LootContextParams.ATTACKING_ENTITY, _dsLootTbl.getEntity())
					.withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, _dsLootTbl.getDirectEntity())
					.withParameter(LootContextParams.ORIGIN, _entLootTbl.position())
					.withParameter(LootContextParams.BLOCK_STATE, _entLootTbl.level().getBlockState(_entLootTbl.blockPosition()))
					.withOptionalParameter(LootContextParams.BLOCK_ENTITY, _entLootTbl.level().getBlockEntity(_entLootTbl.blockPosition()))
					.withParameter(LootContextParams.TOOL, _entLootTbl instanceof Player _player ? _player.getInventory().getSelectedItem() : _entLootTbl.getUseItem())
					.withParameter(LootContextParams.EXPLOSION_RADIUS, 0f)
					.withLuck(_entLootTbl instanceof Player _player ? _player.getLuck() : 0)
					.create(LootContextParamSets.EMPTY))) {
		${statement$foreach}
	}
}
<#-- @formatter:on -->