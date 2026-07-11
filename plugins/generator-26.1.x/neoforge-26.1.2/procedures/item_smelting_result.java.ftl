<#include "mcitems.ftl">
<@addTemplate file="utils/item/item_smelting_result.java.ftl"/>
/*@ItemStack*/(getItemStackSmeltingResult(world, ${mappedMCItemToItemStackCode(input$item, 1)}))