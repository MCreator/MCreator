<#include "mcitems.ftl">
(${mappedMCItemToItem(input$item)}.isFood() ? ${mappedMCItemToItem(input$item)}.getFood().getHealing() : 0)