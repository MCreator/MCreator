<#include "mcitems.ftl">
${mappedMCItemToItemStackCode(input$item, 1)}.setCount(${opt.toInt(input$amount)});