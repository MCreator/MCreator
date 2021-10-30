<#include "mcelements.ftl">
/*@ItemStack*/((${input$entity} instanceof LivingEntity)?((LivingEntity)${input$entity}).getItemStackFromSlot(${toArmorSlot(input$slotid)}):ItemStack.EMPTY)