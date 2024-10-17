<#include "mcitems.ftl">
(EnchantmentHelper.getEnchantmentLevel(Enchantments.${generator.map(field$enhancement, "enhancements")}, ${mappedMCItemToItemStackCode(input$item, 1)}))