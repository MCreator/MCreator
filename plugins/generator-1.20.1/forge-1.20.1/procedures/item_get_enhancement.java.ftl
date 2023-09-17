<#include "mcitems.ftl">
/*@int*/(${mappedMCItemToItemStackCode(input$item, 1)}.getEnchantmentLevel(${generator.map(field$enhancement, "enchantments")}))