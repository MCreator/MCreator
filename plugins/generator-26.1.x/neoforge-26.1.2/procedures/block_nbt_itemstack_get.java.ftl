<#include "mcelements.ftl">
<@addTemplate file="utils/block_nbt/get_itemstack.java.ftl"/>
/*@ItemStack*/(getBlockNBTItemStack(world, ${toBlockPos(input$x, input$y, input$z)}, ${input$tagName}))