<#include "mcelements.ftl">
<#include "mcitems.ftl">
(BlockTags.getCollection().getTagByID(${toResourceLocation(input$b)}).contains(${mappedBlockToBlock(input$a)}))