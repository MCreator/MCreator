<#include "mcelements.ftl">
(ForgeRegistries.BLOCKS.tags().getTag(BlockTags.create(${toResourceLocation(input$tag)})).getRandomElement(RandomSource.create()).orElseGet(() -> Blocks.AIR))