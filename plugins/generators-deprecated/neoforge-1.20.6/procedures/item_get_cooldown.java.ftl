<#include "mcitems.ftl">
/*@float*/(${input$entity} instanceof Player _plrCldRem${cbi} ? _plrCldRem${cbi}.getCooldowns().getCooldownPercent(${mappedMCItemToItem(input$item)}, 0f) * 100 : 0)