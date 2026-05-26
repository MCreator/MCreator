<#include "mcelements.ftl">
<@addTemplate file="utils/block_inventory/get_slot_stack_limit.java.ftl"/>
/*@int*/(getBlockInventorySlotStackLimit(world, ${toBlockPos(input$x, input$y, input$z)}, ${opt.toInt(input$slotid)}))