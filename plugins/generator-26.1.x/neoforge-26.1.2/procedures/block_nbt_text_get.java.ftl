<#include "mcelements.ftl">
<@addTemplate file="utils/block_nbt/get_text.java.ftl"/>
(getBlockNBTString(world, ${toBlockPos(input$x, input$y, input$z)}, ${input$tagName}))