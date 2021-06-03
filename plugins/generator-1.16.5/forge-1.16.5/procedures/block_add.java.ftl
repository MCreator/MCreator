<#include "mcitems.ftl">
world.setBlockState(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}), ${mappedBlockToBlockStateCode(input$block)},3);