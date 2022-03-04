<#include "mcitems.ftl">
/*@ItemStack*/(EnchantmentHelper.enchantItem(new Random(), ${mappedMCItemToItemStackCode(input$item, 1)}, ${opt.toInt(input$levels)}, ${input$treasure}))