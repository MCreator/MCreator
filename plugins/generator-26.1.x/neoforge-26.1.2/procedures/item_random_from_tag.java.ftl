<#include "mcelements.ftl">
(BuiltInRegistries.ITEM.getRandomElementOf(ItemTags.create(${toIdentifier(input$tag)}), RandomSource.create())
		.orElseGet(() -> BuiltInRegistries.ITEM.wrapAsHolder(Items.AIR)).value())