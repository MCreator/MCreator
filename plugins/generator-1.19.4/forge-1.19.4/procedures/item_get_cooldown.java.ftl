<#include "mcitems.ftl">
(${input$entity} instanceof Player _plrCldRem${cbi} ? _plrCldRem${cbi}.getCooldowns().getCooldownPercent(${mappedMCItemToItem(input$item)}, 0f) * 100d : 0d)