<#include "mcitems.ftl">
((entity instanceof EntityPlayer)?((EntityPlayer)entity).inventory.hasItemStack(${mappedMCItemToItemStackCode(input$item, 1)}):false)