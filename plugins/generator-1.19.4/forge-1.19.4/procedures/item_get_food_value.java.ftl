<#include "mcitems.ftl">
/*@int*/(${mappedMCItemToItem(input$item)}.isEdible() ? ${mappedMCItemToItem(input$item)}.getFoodProperties().getNutrition() : 0)