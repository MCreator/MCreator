<#include "mcitems.ftl">
(${input$entity} instanceof Player _plrCldRem${cbi} ? (double) _plrCldRem${cbi}.getCooldowns().getCooldownPercent(${mappedMCItemToItem(input$item)}, 0f) : 0d)