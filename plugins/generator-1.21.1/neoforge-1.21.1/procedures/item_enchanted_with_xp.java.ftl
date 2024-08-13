<#include "mcitems.ftl">
/*@ItemStack*/(EnchantmentHelper.enchantItem(
	world.getRandom(),
	${mappedMCItemToItemStackCode(input$item, 1)},
	${opt.toInt(input$levels)},
	(${input$treasure}) ?
		world.registryAccess().registryOrThrow(Registries.ENCHANTMENT).holders().map(reference -> (Holder<Enchantment>) reference) :
		world.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getTag(EnchantmentTags.IN_ENCHANTING_TABLE).get().stream()
))