<#include "mcitems.ftl">
/*@ItemStack*/(EnchantmentHelper.addRandomEnchantment(new Random(), ${mappedMCItemToItemStackCode(input$item, 1)}, (int) ${input$levels}, ${input$treasure}))