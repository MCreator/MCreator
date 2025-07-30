<#include "mcelements.ftl">
(BuiltInRegistries.BLOCK.getRandomElementOf(BlockTags.create(${toResourceLocation(input$tag)}), RandomSource.create())
	.orElseGet(() -> BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.AIR)).value())