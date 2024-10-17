<#include "mcitems.ftl">
world.playEvent(2001, new BlockPos((int)${input$x},(int)${input$y},(int)${input$z}), Block.getStateId(${mappedBlockToBlockStateCode(input$block)}));