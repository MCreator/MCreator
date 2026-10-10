<#include "mcelements.ftl">
<@addTemplate file="utils/world/data_block_direction_at.java.ftl"/>
(getBlockDirection(world, ${toBlockPos(input$x,input$y,input$z)}))