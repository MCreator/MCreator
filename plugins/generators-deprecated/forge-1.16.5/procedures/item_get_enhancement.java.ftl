<#include "mcitems.ftl">
(EnchantmentHelper.getEnchantmentLevel(${generator.map(field$enhancement, "enchantments")}, ${mappedMCItemToItemStackCode(input$item, 1)}))