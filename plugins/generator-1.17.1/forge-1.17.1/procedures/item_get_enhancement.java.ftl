<#include "mcitems.ftl">
/*@int*/(EnchantmentHelper.getItemEnchantmentLevel(${generator.map(field$enhancement, "enchantments")}, ${mappedMCItemToItemStackCode(input$item, 1)}))