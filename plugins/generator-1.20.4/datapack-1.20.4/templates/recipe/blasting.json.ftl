<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
    "type": "minecraft:blasting",
    <#if data.group?has_content>"group": "${data.group}",</#if>
    "category": "${data.cookingBookCategory?lower_case}",
    "experience": ${data.xpReward},
    "cookingtime": ${data.cookingTime},
    "ingredient": {
      ${mappedMCItemToItemObjectJSON(data.blastingInputStack)}
    },
    "result": "${mappedMCItemToRegistryName(data.blastingReturnStack)}"
}
<#-- @formatter:on -->