<#include "mcelements.ftl">
if (${input$entity} instanceof LivingEntity _ent${customBlockIndex}) {
	MinecraftServer _server${customBlockIndex} = ServerLifecycleHooks.getCurrentServer();
	if (_server${customBlockIndex} != null) {
		try {
			for (ItemStack itemstackiterator : _server${customBlockIndex}.getLootTables().get(${toResourceLocation(input$location)}).getRandomItems(((LootContext.Builder) ObfuscationReflectionHelper.findMethod(LivingEntity.class, "m_7771_", boolean.class, DamageSource.class)
					.invoke(_ent${customBlockIndex}, true, (_ent${customBlockIndex}.getLastDamageSource() != null ? _ent${customBlockIndex}.getLastDamageSource() : DamageSource.GENERIC)))
					.withParameter(LootContextParams.BLOCK_STATE, _ent${customBlockIndex}.level.getBlockState(_ent${customBlockIndex}.blockPosition()))
					.withOptionalParameter(LootContextParams.BLOCK_ENTITY, _ent${customBlockIndex}.level.getBlockEntity(_ent${customBlockIndex}.blockPosition()))
					.withParameter(LootContextParams.TOOL, _ent${customBlockIndex} instanceof Player _plr${customBlockIndex} ? _plr${customBlockIndex}.getInventory().getSelected() : _ent${customBlockIndex}.getUseItem())
					.withParameter(LootContextParams.EXPLOSION_RADIUS, 0F).create(LootContextParamSets.ALL_PARAMS))) {
				${statement$foreach}
			}
		} catch (Exception _ignored) {
		}
	}
}