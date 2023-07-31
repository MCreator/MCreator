<#include "mcitems.ftl">
(${input$entity} instanceof Player _plrCldRem ? (double) _plrCldRem.getCooldowns().getCooldownPercent(${mappedMCItemToItem(input$item)}, 0f) : 0d)