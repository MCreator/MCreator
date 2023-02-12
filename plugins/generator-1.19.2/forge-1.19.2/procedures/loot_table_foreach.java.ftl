<#include "mcelements.ftl">
if (${input$entity} instanceof LivingEntity _entity && !_entity.level.isClientSide() && _entity.getServer() != null) {
	try {
		for (ItemStack itemstackiterator : _entity.getServer().getLootTables().get(${toResourceLocation(input$location)})
				.getRandomItems(((LootContext.Builder) ObfuscationReflectionHelper.findMethod(LivingEntity.class, "m_7771_", boolean.class, DamageSource.class)
						.invoke(_entity, true, (_entity.getLastDamageSource() != null ? _entity.getLastDamageSource() : DamageSource.GENERIC)))
						.withParameter(LootContextParams.BLOCK_STATE, _entity.level.getBlockState(_entity.blockPosition()))
						.withOptionalParameter(LootContextParams.BLOCK_ENTITY, _entity.level.getBlockEntity(_entity.blockPosition()))
						.withParameter(LootContextParams.TOOL, _entity instanceof Player _player ? _player.getInventory().getSelected() : _entity.getUseItem())
						.withParameter(LootContextParams.EXPLOSION_RADIUS, 0F).create(LootContextParamSets.EMPTY))) {
			${statement$foreach}
		}
	} catch (Exception _ignored) {
	}
}