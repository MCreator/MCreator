<#include "mcitems.ftl">
<#if generator.map(field$blocktag, "tags")?has_content>
	<#if generator.map(field$blocktag, "tags")??>
	(${mappedBlockToBlockStateCode(input$a)}.is(BlockTags.create(new ResourceLocation("${generator.map(field$blocktag, "tags")?replace("CUSTOM:", "")}"))))
	<#else>
	(${mappedBlockToBlockStateCode(input$a)}.is(BlockTags.create(new ResourceLocation("${field$blocktag}"))))
	</#if>
<#else>(false)</#if>