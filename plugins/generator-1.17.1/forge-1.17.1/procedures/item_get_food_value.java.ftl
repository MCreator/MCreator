<#include "mcitems.ftl">
(${mappedMCItemToItem(input$item)}.isEdible() ? ${mappedMCItemToItem(input$item)}.getFoodProperties().getNutrition() : 0)