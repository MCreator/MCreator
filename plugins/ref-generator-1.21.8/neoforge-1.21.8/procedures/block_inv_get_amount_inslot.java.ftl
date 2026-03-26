<#include "mcelements.ftl">
<@addTemplate file="utils/block_inventory/get_item.java.ftl"/>
/*@int*/(itemFromBlockInventory(world, ${toBlockPos(input$x, input$y, input$z)}, ${opt.toInt(input$slotid)}).getCount())