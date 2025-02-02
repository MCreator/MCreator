<#include "mcitems.ftl">
/*@BlockState*/(${mappedMCItemToItem(input$source)} instanceof BucketItem _bucket ? _bucket.content.defaultFluidState().createLegacyBlock() : Blocks.AIR.defaultBlockState())