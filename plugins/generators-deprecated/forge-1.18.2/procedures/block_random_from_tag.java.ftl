<#include "mcelements.ftl">
(ForgeRegistries.BLOCKS.tags().getTag(BlockTags.create(${toResourceLocation(input$tag)})).getRandomElement(new Random()).orElseGet(() -> Blocks.AIR))