<#include "mcitems.ftl">
(${mappedMCItemToItemStackCode(input$item, 1)}).addEnchantment(${generator.map(field$enhancement, "enchantments")},(int) ${input$level});