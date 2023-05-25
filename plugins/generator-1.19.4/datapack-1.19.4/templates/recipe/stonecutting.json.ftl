<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
    "type": "minecraft:stonecutting",
    <#if data.group?has_content>"group": "${data.group}",</#if>
    "count": ${data.recipeRetstackSize},
    "ingredient": {
        ${mappedMCItemToIngameItemName(data.stoneCuttingInputStack)}
    },
    "result": "${mappedMCItemToIngameNameNoTags(data.stoneCuttingReturnStack)}"
}
<#-- @formatter:on -->