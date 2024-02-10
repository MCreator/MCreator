<#include "mcitems.ftl">
/*@ItemStack*/(${mappedBlockToBlock(input$block)} instanceof LiquidBlock _liquid ? new ItemStack(_liquid.getFluid().getBucket()) : ItemStack.EMPTY)