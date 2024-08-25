<#include "mcelements.ftl">
(BuiltInRegistries.BLOCK.getOrCreateTag(BlockTags.create(${toResourceLocation(input$tag)})).getRandomElement(RandomSource.create())
	.orElseGet(() -> BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.AIR)).value())