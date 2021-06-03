<#include "mcitems.ftl">
((${input$entity} instanceof PlayerEntity)?((PlayerEntity)${input$entity}).inventory.hasItemStack(${mappedMCItemToItemStackCode(input$item, 1)}):false)