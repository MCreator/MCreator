<#include "mcitems.ftl">
${mappedMCItemToItemStackCode(input$item, 1)}.grow(${opt.toInt(input$amount)});