<#include "mcitems.ftl">
(${mappedMCItemToItem(input$item)}.isEdible() ? ${mappedMCItemToItem(input$item)}.getFoodProperties().getSaturationModifier() : 0)