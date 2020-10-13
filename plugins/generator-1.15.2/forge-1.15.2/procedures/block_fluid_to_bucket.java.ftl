<#include "mcitems.ftl">
/*@ItemStack*/new ItemStack((${mappedBlockToBlockStateCode(input$block)}.getBlock() instanceof FlowingFluidBlock ?
((FlowingFluidBlock) ${mappedBlockToBlockStateCode(input$block)}.getBlock()).getFluid().getFilledBucket() : Items.AIR))