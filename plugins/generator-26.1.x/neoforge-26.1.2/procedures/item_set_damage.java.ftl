<#include "mcitems.ftl">
${mappedMCItemToItemStackCode(input$item, 1)}.setDamageValue(${opt.toInt(input$amount)});