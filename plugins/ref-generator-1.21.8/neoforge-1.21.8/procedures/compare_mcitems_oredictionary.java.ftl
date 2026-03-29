<#include "mcelements.ftl">
<#include "mcitems.ftl">
(${mappedMCItemToItemStackCode(input$a, 1)}.is(ItemTags.create(${toIdentifier(input$b)})))