<#include "mcelements.ftl">
<#include "mcitems.ftl">
(BlockTags.getAllTags().getTagOrEmpty(${toResourceLocation(input$b)}).contains(${mappedBlockToBlock(input$a)}))