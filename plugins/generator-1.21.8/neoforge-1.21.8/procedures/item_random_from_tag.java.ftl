<#include "mcelements.ftl">
(BuiltInRegistries.ITEM.getRandomElementOf(ItemTags.create(${toResourceLocation(input$tag)}), RandomSource.create())
		.orElseGet(() -> BuiltInRegistries.ITEM.wrapAsHolder(Items.AIR)).value())