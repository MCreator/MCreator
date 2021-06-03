<#include "mcitems.ftl">
/*@ItemStack*/(${mappedBlockToBlockStateCode(input$block)}.getBlock() instanceof FlowingFluidBlock ?
        new ItemStack(((FlowingFluidBlock) ${mappedBlockToBlockStateCode(input$block)}.getBlock()).getFluid().getFilledBucket()) : ItemStack.EMPTY)