<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
    "type": "minecraft:stonecutting",
    <#if data.group?has_content>"group": "${data.group}",</#if>
    "count": ${data.recipeRetstackSize},
    "ingredient": {
        ${mappedMCItemToItemObjectJSON(data.stoneCuttingInputStack)}
    },
    "result": {
        ${mappedMCItemToItemObjectJSON(data.stoneCuttingReturnStack, "id")}
    }
}
<#-- @formatter:on -->