<#include "mcitems.ftl">
if(${input$entity} instanceof PlayerEntity) {
	ItemStack _stktoremove = ${mappedMCItemToItemStackCode(input$item, 1)};
	((PlayerEntity)${input$entity}).inventory
        .clearMatchingItems(p -> _stktoremove.getItem() == p.getItem(), (int)${input$amount});
}