<#include "mcitems.ftl">
(${mappedMCItemToItem(input$item)}.isFood() ? ${mappedMCItemToItem(input$item)}.getFood().getSaturation() : 0)