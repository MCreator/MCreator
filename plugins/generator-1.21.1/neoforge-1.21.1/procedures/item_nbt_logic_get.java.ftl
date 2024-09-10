<#include "mcitems.ftl">
(${mappedMCItemToItemStackCode(input$item, 1)}.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean(${input$tagName}))