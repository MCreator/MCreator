<#include "mcitems.ftl">
/*@int*/(${mappedMCItemToItem(input$item)} instanceof TieredItem _tierItem ? _tierItem.getTier().getLevel() : 0)