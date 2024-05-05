<#include "mcitems.ftl">
/*@float*/(${mappedMCItemToItemStackCode(input$item)}.has(DataComponents.FOOD) ? ${mappedMCItemToItemStackCode(input$item)}.getFoodProperties(null).saturation() : 0)