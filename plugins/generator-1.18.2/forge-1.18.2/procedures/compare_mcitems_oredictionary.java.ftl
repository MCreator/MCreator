<#include "mcitems.ftl">
<#if generator.map(field$itemtag, "tags") != "null">
(${mappedMCItemToItemStackCode(input$a, 1)}.is(ItemTags.create(new ResourceLocation(${generator.map(field$itemtag, "tags")}))))
<#else>(false)</#if>