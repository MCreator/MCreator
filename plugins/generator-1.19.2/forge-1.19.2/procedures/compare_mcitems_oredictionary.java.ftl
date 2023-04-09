<#include "mcitems.ftl">
<#if generator.map(field$itemtag, "tags")?has_content>
	<#if generator.map(field$itemtag, "tags")??>
	(${mappedMCItemToItemStackCode(input$a, 1)}.is(ItemTags.create(new ResourceLocation(${generator.map(field$itemtag, "tags")?replace("CUSTOM:", "")}"))))
	<#else>
	(${mappedMCItemToItemStackCode(input$a, 1)}.is(ItemTags.create(new ResourceLocation(${field$itemtag}"))))
	</#if>
<#else>(false)</#if>