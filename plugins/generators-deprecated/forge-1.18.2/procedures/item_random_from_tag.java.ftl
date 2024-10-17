<#include "mcelements.ftl">
(ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(${toResourceLocation(input$tag)})).getRandomElement(new Random()).orElseGet(() -> Items.AIR))