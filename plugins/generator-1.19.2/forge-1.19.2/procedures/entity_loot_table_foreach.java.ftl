<#include "mcelements.ftl">
<#-- @formatter:off -->
if (${input$entity} instanceof LivingEntity _ent${customBlockIndex} && !_ent${customBlockIndex}.level.isClientSide() && _ent${customBlockIndex}.getServer() != null) {
	DamageSource _ds${customBlockIndex} = _ent${customBlockIndex}.getLastDamageSource();
	if (_ds${customBlockIndex} == null) _ds${customBlockIndex} = DamageSource.GENERIC;
	for (ItemStack itemstackiterator : _ent${customBlockIndex}.getServer().getLootTables().get(${toResourceLocation(input$location)})
			.getRandomItems(new LootContext.Builder((ServerLevel) _ent${customBlockIndex}.level)
					.withParameter(LootContextParams.THIS_ENTITY, _ent${customBlockIndex})
					.withOptionalParameter(LootContextParams.LAST_DAMAGE_PLAYER, _ent${customBlockIndex}.getLastHurtByMob() instanceof Player _player ?  _player : null)
					.withParameter(LootContextParams.DAMAGE_SOURCE, _ds${customBlockIndex})
					.withOptionalParameter(LootContextParams.KILLER_ENTITY, _ds${customBlockIndex}.getEntity())
					.withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, _ds${customBlockIndex}.getDirectEntity())
					.withParameter(LootContextParams.ORIGIN, _ent${customBlockIndex}.position())
					.withParameter(LootContextParams.BLOCK_STATE, _ent${customBlockIndex}.level.getBlockState(_ent${customBlockIndex}.blockPosition()))
					.withOptionalParameter(LootContextParams.BLOCK_ENTITY, _ent${customBlockIndex}.level.getBlockEntity(_ent${customBlockIndex}.blockPosition()))
					.withParameter(LootContextParams.TOOL, _ent${customBlockIndex} instanceof Player _player ? _player.getInventory().getSelected() : _ent${customBlockIndex}.getUseItem())
					.withParameter(LootContextParams.EXPLOSION_RADIUS, 0f)
					.withLuck(_ent${customBlockIndex} instanceof Player _player ? _player.getLuck() : 0)
					.create(LootContextParamSets.EMPTY))) {
		${statement$foreach}
	}
}
<#-- @formatter:on -->