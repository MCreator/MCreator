<#include "mcitems.ftl">
/*@ItemStack*/(EnchantmentHelper.enchantItem(RandomSource.create(), ${mappedMCItemToItemStackCode(input$item, 1)}, ${opt.toInt(input$levels)}, ${input$treasure}))