<#include "mcelements.ftl">
<#include "mcitems.ftl">
<@addTemplate file="utils/block_inventory/can_insert.java.ftl"/>
(canInsertInBlockInventory(world, ${toBlockPos(input$x, input$y, input$z)}, ${opt.toInt(input$slotid)}, ${opt.toInt(input$amount)}, ${mappedMCItemToItemStackCode(input$item, 1)}))