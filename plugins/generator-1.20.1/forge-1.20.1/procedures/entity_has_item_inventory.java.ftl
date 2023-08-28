<#include "mcitems.ftl">
(${input$entity} instanceof Player _playerHasItem && _playerHasItem.getInventory().contains(${mappedMCItemToItemStackCode(input$item, 1)}))