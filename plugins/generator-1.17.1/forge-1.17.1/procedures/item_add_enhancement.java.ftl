<#include "mcitems.ftl">
(${mappedMCItemToItemStackCode(input$item, 1)}).enchant(${generator.map(field$enhancement, "enchantments")},(int) ${input$level});