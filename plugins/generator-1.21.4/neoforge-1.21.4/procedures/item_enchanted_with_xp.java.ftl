<#include "mcitems.ftl">
/*@ItemStack*/(EnchantmentHelper.enchantItem(
	world.getRandom(),
	${mappedMCItemToItemStackCode(input$item, 1)},
	${opt.toInt(input$levels)},
	(${input$treasure}) ?
		world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).listElements().map(reference -> (Holder<Enchantment>) reference) :
		world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(EnchantmentTags.IN_ENCHANTING_TABLE).get().stream()
))