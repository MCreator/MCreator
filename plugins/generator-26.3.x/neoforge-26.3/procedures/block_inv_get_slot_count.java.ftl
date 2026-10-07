<#include "mcelements.ftl">
<@addTemplate file="utils/block_inventory/get_slot_count.java.ftl"/>
/*@int*/(getBlockInventorySlotCount(world, ${toBlockPos(input$x, input$y, input$z)}))