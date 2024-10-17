<#-- @formatter:off -->
<#include "../../mcitems.ftl">
{
    <#if data.group?has_content>"group": "${data.group}",</#if>
    "type": "minecraft:stonecutting",
    "count": ${data.recipeRetstackSize},
    "ingredient": {
        ${mappedMCItemToIngameItemName(data.stoneCuttingInputStack)}
    },
    "result": "${mappedMCItemToIngameNameNoTags(data.stoneCuttingReturnStack)}"
}
<#-- @formatter:on -->