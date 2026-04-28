<#include "mcitems.ftl">
/*@ItemStack*/(${mappedBlockToBlock(input$block)} instanceof LiquidBlock _liquid ? new ItemStack(_liquid.fluid.getBucket()) : ItemStack.EMPTY)