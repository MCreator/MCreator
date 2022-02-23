<#include "mcitems.ftl">
/*@BlockState*/(${mappedMCItemToItem(input$source)} instanceof BlockItem _bi ? _bi.getBlock().defaultBlockState() : Blocks.AIR.defaultBlockState())