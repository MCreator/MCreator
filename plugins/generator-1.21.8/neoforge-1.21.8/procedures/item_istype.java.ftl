<#include "mcitems.ftl">
<#if field$item_type == "Sword">
(${mappedMCItemToItemStackCode(input$item)}.is(ItemTags.SWORDS))
<#elseif field$item_type == "Pickaxe">
(${mappedMCItemToItemStackCode(input$item)}.is(ItemTags.PICKAXES))
<#elseif field$item_type == "Armor">
(${mappedMCItemToItemStackCode(input$item)}.has(DataComponents.EQUIPPABLE))
<#else>
(${mappedMCItemToItem(input$item)} instanceof ${generator.map(field$item_type, "itemtypes")}Item)
</#if>