<#include "mcelements.ftl">
(BuiltInRegistries.ITEM.getOrCreateTag(ItemTags.create(${toResourceLocation(input$tag)})).getRandomElement(RandomSource.create())
		.orElseGet(() -> BuiltInRegistries.ITEM.wrapAsHolder(Items.AIR)).value())