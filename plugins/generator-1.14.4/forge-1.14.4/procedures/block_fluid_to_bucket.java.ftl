<#include "mcitems.ftl">
(${mappedBlockToBlockStateCode(input$block)}.getBlock() instanceof FlowingFluidBlock ?
((FlowingFluidBlock) ${mappedBlockToBlockStateCode(input$block)}.getBlock()).getFluid().getFilledBucket() : Items.AIR)