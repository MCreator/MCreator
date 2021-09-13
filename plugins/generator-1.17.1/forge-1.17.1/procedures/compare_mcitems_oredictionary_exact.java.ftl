<#include "mcelements.ftl">
<#include "mcitems.ftl">
(ItemTags.getAllTags().getTagOrEmpty(${toResourceLocation(input$b)}).contains(${mappedMCItemToItem(input$a)}))