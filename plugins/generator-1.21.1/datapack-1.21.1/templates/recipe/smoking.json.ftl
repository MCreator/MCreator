<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
    "type": "minecraft:smoking",
    <#if data.group?has_content>"group": "${data.group}",</#if>
    "category": "${data.cookingBookCategory?lower_case}",
    "experience": ${data.xpReward},
    "cookingtime": ${data.cookingTime},
    "ingredient": {
      ${mappedMCItemToItemObjectJSON(data.smokingInputStack)}
    },
    "result": {
        ${mappedMCItemToItemObjectJSON(data.smokingReturnStack, "id")}
    }
}
<#-- @formatter:on -->