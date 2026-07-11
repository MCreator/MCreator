<#include "mcelements.ftl">
<@addTemplate file="utils/block_nbt/get_logic.java.ftl"/>
(getBlockNBTLogic(world, ${toBlockPos(input$x, input$y, input$z)}, ${input$tagName}))