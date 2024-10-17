<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
    "group": "<#if data.group?has_content>${data.group}<#else>${modid}</#if>",
    "type": "minecraft:stonecutting",
    "count": ${data.recipeRetstackSize},
    "ingredient": {
        ${mappedMCItemToIngameItemName(data.stoneCuttingInputStack)}
    },
    "result": ${mappedMCItemToIngameItemName(data.stoneCuttingReturnStack)?replace("\"item\":", "")}
}
<#-- @formatter:on -->