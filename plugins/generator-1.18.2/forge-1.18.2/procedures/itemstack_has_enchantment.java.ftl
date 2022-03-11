<#include "mcitems.ftl">
(EnchantmentHelper.getItemEnchantmentLevel(${generator.map(field$enhancement, "enchantments")}, ${mappedMCItemToItemStackCode(input$item, 1)}) != 0)