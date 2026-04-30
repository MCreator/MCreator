<#include "mcelements.ftl">
<#include "mcitems.ftl">
<@addTemplate file="utils/block_inventory/insert_item.java.ftl"/>
(insertInBlockInventory(world, ${toBlockPos(input$x, input$y, input$z)}, ${opt.toInt(input$slotid)}, ${opt.toInt(input$amount)}, ${mappedMCItemToItemStackCode(input$item, 1)}, ${field$simulated?lower_case}))