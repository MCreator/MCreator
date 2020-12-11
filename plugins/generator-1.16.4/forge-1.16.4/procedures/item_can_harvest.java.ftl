<#include "mcitems.ftl">
(${mappedMCItemToItemStackCode(input$item, 1)}.getItem().canHarvestBlock(${mappedBlockToBlockStateCode(input$block)}))