<#-- @formatter:off -->
<#include "../mcitems_json.ftl">
{
    "type": "minecraft:campfire_cooking",
    <#if data.group?has_content>"group": "${data.group}",</#if>
    "category": "${data.cookingBookCategory?lower_case}",
    "experience": ${data.xpReward},
    "cookingtime": ${data.cookingTime},
    "ingredient": {
      ${mappedMCItemToItemObjectJSON(data.campfireCookingInputStack)}
    },
    "result": {
        ${mappedMCItemToItemObjectJSON(data.campfireCookingReturnStack, "id")}
    }
}
<#-- @formatter:on -->