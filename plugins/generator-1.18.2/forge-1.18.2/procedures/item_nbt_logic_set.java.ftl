<#include "mcitems.ftl">
${mappedMCItemToItemStackCode(input$item, 1)}.getOrCreateTag().putBoolean(${input$tagName}, ${input$tagValue});