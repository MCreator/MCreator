<#include "mcelements.ftl">
(ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(${toResourceLocation(input$tag)})).getRandomElement(RandomSource.create()).orElseGet(() -> Items.AIR))