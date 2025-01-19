<#include "mcitems.ftl">
<@addTemplate file="utils/itemhandler_get_slot.java.ftl"/>
/*@ItemStack*/(getItemStackFromItemStackSlot(${opt.toInt(input$slotid)}, ${mappedMCItemToItemStackCode(input$item, 1)}))