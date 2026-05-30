<#include "mcitems.ftl">
(${input$entity} instanceof Player _plrCldCheck${cbi} && _plrCldCheck${cbi}.getCooldowns().isOnCooldown(${mappedMCItemToItemStackCode(input$item)}))