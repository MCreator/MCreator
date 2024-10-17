<#include "mcitems.ftl">
if(entity instanceof EntityPlayer)
	((EntityPlayer)entity).inventory
        .clearMatchingItems(${mappedMCItemToItem(input$item)}, ${getMappedMCItemMetadata(input$item)},(int)${input$amount},null);