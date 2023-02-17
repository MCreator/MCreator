<#include "mcelements.ftl">
<#-- @formatter:off -->
if (${input$entity} instanceof LivingEntity _entity && !_entity.level.isClientSide() && _entity.getServer() != null) {
	DamageSource _ds = _entity.getLastDamageSource();
	if (_ds == null) _ds = DamageSource.GENERIC;
	for (ItemStack itemstackiterator : _entity.getServer().getLootTables().get(${toResourceLocation(input$location)})
			.getRandomItems(new LootContext.Builder((ServerLevel) _entity.level)
					.withParameter(LootContextParams.THIS_ENTITY, _entity)
					.withOptionalParameter(LootContextParams.LAST_DAMAGE_PLAYER, _entity.getLastHurtByMob() instanceof Player _player ?  _player : null)
					.withParameter(LootContextParams.DAMAGE_SOURCE, _ds)
					.withOptionalParameter(LootContextParams.KILLER_ENTITY, _ds.getEntity())
					.withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, _ds.getDirectEntity())
					.withParameter(LootContextParams.ORIGIN, _entity.position())
					.withParameter(LootContextParams.BLOCK_STATE, _entity.level.getBlockState(_entity.blockPosition()))
					.withOptionalParameter(LootContextParams.BLOCK_ENTITY, _entity.level.getBlockEntity(_entity.blockPosition()))
					.withParameter(LootContextParams.TOOL, _entity instanceof Player _player ? _player.getInventory().getSelected() : _entity.getUseItem())
					.withParameter(LootContextParams.EXPLOSION_RADIUS, 0f)
					.withLuck(_entity instanceof Player _player ? _player.getLuck() : 0)
					.create(LootContextParamSets.EMPTY))) {
		${statement$foreach}
	}
}
<#-- @formatter:on -->