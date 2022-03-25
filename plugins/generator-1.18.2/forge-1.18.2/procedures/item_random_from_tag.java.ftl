<#include "mcelements.ftl">
(Registry.ITEM.getTag(ItemTags.create(${toResourceLocation(input$tag)}))
    .flatMap(holderSet -> holderSet.getRandomElement(new Random())).map(Holder::value).orElseGet(() -> Items.AIR))