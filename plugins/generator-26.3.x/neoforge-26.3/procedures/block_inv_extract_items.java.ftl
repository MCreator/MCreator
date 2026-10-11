<#include "mcelements.ftl">
<@addTemplate file="utils/block_inventory/extract_item.java.ftl"/>
extractFromBlockInventory(world, ${toBlockPos(input$x, input$y, input$z)}, ${opt.toInt(input$slotid)}, ${opt.toInt(input$amount)}, false);