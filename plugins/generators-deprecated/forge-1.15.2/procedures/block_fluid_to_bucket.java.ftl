<#include "mcitems.ftl">
/*@ItemStack*/(${mappedBlockToBlock(input$block)} instanceof FlowingFluidBlock ?
        new ItemStack(((FlowingFluidBlock) ${mappedBlockToBlock(input$block)}).getFluid().getFilledBucket()) : ItemStack.EMPTY)