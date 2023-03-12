<#include "mcitems.ftl">
<#if generator.map(field$blocktag, "tags") != "null">
(${mappedBlockToBlockStateCode(input$a)}.is(BlockTags.create(new ResourceLocation("${generator.map(field$blocktag, "tags")}"))))
<#else>(false)</#if>