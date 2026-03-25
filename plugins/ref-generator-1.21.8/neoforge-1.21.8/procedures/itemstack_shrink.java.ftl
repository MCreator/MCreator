<#include "mcitems.ftl">
${mappedMCItemToItemStackCode(input$item, 1)}.shrink(${opt.toInt(input$amount)});