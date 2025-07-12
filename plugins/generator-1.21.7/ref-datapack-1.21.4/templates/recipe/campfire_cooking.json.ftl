<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
    "type": "minecraft:campfire_cooking",
    <#if data.group?has_content>"group": "${data.group}",</#if>
    "category": "${data.cookingBookCategory?lower_case}",
    "experience": ${data.xpReward},
    "cookingtime": ${data.cookingTime},
    "ingredient": "${mappedMCItemToRegistryName(data.campfireCookingInputStack, true)}",
    "result": {
        ${mappedMCItemToItemObjectJSON(data.campfireCookingReturnStack, "id")}
    }
}
<#-- @formatter:on -->