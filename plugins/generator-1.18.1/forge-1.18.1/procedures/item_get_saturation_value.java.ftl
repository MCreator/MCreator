<#include "mcitems.ftl">
/*@float*/(${mappedMCItemToItem(input$item)}.isEdible() ? ${mappedMCItemToItem(input$item)}.getFoodProperties().getSaturationModifier() : 0)