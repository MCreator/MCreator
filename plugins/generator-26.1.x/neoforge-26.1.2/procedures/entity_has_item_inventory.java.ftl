<#include "mcitems.ftl">
<@addTemplate file="utils/entity/entity_has_item_inventory.java.ftl"/>
(hasEntityInInventory(${input$entity}, ${mappedMCItemToItemStackCode(input$item, 1)}))