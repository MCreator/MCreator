<#include "mcitems.ftl">
EnchantmentHelper.updateEnchantments(${mappedMCItemToItemStackCode(input$item, 1)}, mutableEnchantments ->
	mutableEnchantments.removeIf(
		enchantment -> enchantment.is(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(${generator.map(field$enhancement, "enchantments")}))
	)
);