<#include "mcitems.ftl">
if(${input$entity} instanceof PlayerEntity)
	((PlayerEntity)${input$entity}).inventory
        .clearMatchingItems(p -> ${mappedMCItemToItemStackCode(input$item, 1)}.getItem() == p.getItem(), (int)${input$amount});