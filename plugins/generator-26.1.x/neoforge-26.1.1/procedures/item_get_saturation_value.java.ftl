<#include "mcitems.ftl">
/*@float*/(${mappedMCItemToItemStackCode(input$item)}.has(DataComponents.FOOD) ? ${mappedMCItemToItemStackCode(input$item)}.get(DataComponents.FOOD).saturation() : 0)