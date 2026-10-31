<#include "mcitems.ftl">
<#if field$item_type == "Sword">
(${mappedMCItemToItemStackCode(input$item)}.is(ItemTags.SWORDS))
<#elseif field$item_type == "Pickaxe">
(${mappedMCItemToItemStackCode(input$item)}.is(ItemTags.PICKAXES))
<#elseif field$item_type == "Axe">
(${mappedMCItemToItemStackCode(input$item)}.is(ItemTags.AXES))
<#elseif field$item_type == "Shovel">
(${mappedMCItemToItemStackCode(input$item)}.is(ItemTags.SHOVELS))
<#elseif field$item_type == "Hoe">
(${mappedMCItemToItemStackCode(input$item)}.is(ItemTags.HOES))
<#elseif field$item_type == "Armor">
(${mappedMCItemToItemStackCode(input$item)}.has(DataComponents.EQUIPPABLE))
<#elseif field$item_type == "Tool">
(${mappedMCItemToItemStackCode(input$item)}.has(DataComponents.TOOL))
<#elseif field$item_type == "Bed">
(${mappedMCItemToItemStackCode(input$item)}.is(ItemTags.BEDS))
<#else>
(${mappedMCItemToItem(input$item)} instanceof ${generator.map(field$item_type, "itemtypes")}Item)
</#if>