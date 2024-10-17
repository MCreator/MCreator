<#include "mcelements.ftl">
<#include "mcitems.ftl">
(ItemTags.getCollection().getTagByID(${toResourceLocation(input$b)}).contains(${mappedMCItemToItem(input$a)}))