<#include "mcitems.ftl">
(${input$entity} instanceof Player _plrCldCheck && _plrCldCheck.getCooldowns().isOnCooldown(${mappedMCItemToItem(input$item)}))