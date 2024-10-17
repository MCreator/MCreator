<#include "mcitems.ftl">
new RandomPatchConfiguration(${field$tries}, ${field$xzSpread}, ${field$ySpread},
    PlacementUtils.filtered(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(${mappedBlockToBlockStateProvider(input$block)}), ${input$condition}))