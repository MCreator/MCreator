<#include "mcitems.ftl">
<@addTemplate file="utils/item/item_smelting_result.java.ftl"/>
/*@ItemStack*/(getItemStackFromItemStackSlot(world, ${mappedMCItemToItemStackCode(input$item, 1)}))