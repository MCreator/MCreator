<#include "mcitems.ftl">
world.levelEvent(2001, new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}), Block.getId(${mappedBlockToBlockStateCode(input$block)}));