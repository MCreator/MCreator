<#include "mcelements.ftl">
(Registry.BLOCK.getTag(BlockTags.create(${toResourceLocation(input$tag)}))
    .flatMap(holderSet -> holderSet.getRandomElement(new Random())).map(Holder::value).orElseGet(() -> Blocks.AIR))