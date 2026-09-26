<#include "mcelements.ftl">
/*@ItemStack*/(${input$entity} instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(${toArmorSlot(input$slotid)}):ItemStack.EMPTY)