<#include "mcelements.ftl">
<@addTemplate file="utils/block_nbt/get_num.java.ftl"/>
(getBlockNBTNumber(world, ${toBlockPos(input$x, input$y, input$z)}, ${input$tagName}))